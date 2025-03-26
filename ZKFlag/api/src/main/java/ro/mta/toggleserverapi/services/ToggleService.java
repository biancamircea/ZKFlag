package ro.mta.toggleserverapi.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.hashids.Hashids;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.*;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.exceptions.ResourceNotFoundException;
import ro.mta.toggleserverapi.exceptions.ToggleNotFoundException;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.util.ZKPVerifier;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ToggleService {
    private final ToggleRepository toggleRepository;
    private final ProjectService projectService;
    private final ToggleEnvironmentService toggleEnvironmentService;
    private final ToggleTagService toggleTagService;
    private final ConstraintConverter constraintConverter;
    private final ConstraintService constraintService;
    private final ToggleEnvironmentConverter toggleEnvironmentConverter;
    private final EventService eventService;
    private final ToggleScheduleConverter toggleScheduleConverter;
    private final InstanceEnvironmentService instanceEnvironmentService;
    private final InstanceService instanceService;
    private final EnvironmentService environmentService;
    private final ConstraintRepository constraintRepository;
    private final ConstraintValueRepository constraintValueRepository;
    private final ProjectRepository projectRepository;
    private final InstanceRepository instanceRepository;
    private final EnvironmentRepository environmentRepository;


    public List<Toggle> fetchAllToggles() {
        return toggleRepository.findAll();
    }

    public Toggle fetchToggle(Long id) {
        return toggleRepository.findById(id)
                .orElseThrow(() -> new ToggleNotFoundException(id));
    }
    public List<Toggle> fetchAllTogglesByProjectId(Long id){
        return toggleRepository.findAllByProjectId(id);
    }

    public Toggle fetchToggleByProjectIdAndToggleId(Long projectId, Long toggleId){
        return toggleRepository.findByIdAndProjectId(toggleId, projectId)
                .orElseThrow(() -> new ToggleNotFoundException(toggleId, projectId));
    }

    public Toggle saveToggle(Toggle toggle, Long projectId) {
        Project project = projectService.fetchProject(projectId);
        toggle.setProject(project);

        Toggle savedToggle = toggleRepository.save(toggle);
        eventService.submitAction(ActionType.CREATE, project, savedToggle);

        List<Instance> instances = instanceService.fetchInstancesByProject(projectId);

        for (Instance instance : instances) {
            List<Environment> activeEnvironments = instanceService.fetchEnabledEnvironmentsInInstance(instance);

            for (Environment environment : activeEnvironments) {
                toggleEnvironmentService.createToggleEnvironmentAssociation(savedToggle, environment, instance);
            }
        }

        if (savedToggle.getHashId() == null || savedToggle.getHashId().isEmpty()) {
            Hashids hashids = new Hashids("toggle-salt", 8);
            savedToggle.setHashId(hashids.encode(savedToggle.getId()));

            savedToggle = toggleRepository.save(savedToggle);
        }

        return savedToggle;
    }

    public TogglesResponseDTO getAllToggles(){
        List<Toggle> toggleList = fetchAllToggles();
        List<ToggleDTO> toggleDTOList = new ArrayList<>();
        for(Toggle toggle : toggleList){
            ToggleDTO toggleDTO = makeToggleDTOFromToggle(toggle);
            toggleDTOList.add(toggleDTO);
        }
        return TogglesResponseDTO.toDTOSimple(toggleDTOList);
    }

    public TogglesResponseDTO getAllTogglesFromProject(Long projectId){
        List<Toggle> toggleList = fetchAllTogglesByProjectId(projectId);
        List<ToggleDTO> toggleDTOList = new ArrayList<>();
        for(Toggle toggle : toggleList){
            ToggleDTO toggleDTO = makeProjectToggleDTOFromToggle(toggle);
            toggleDTOList.add(toggleDTO);
        }
        TogglesResponseDTO togglesResponseDTO = new TogglesResponseDTO();
        togglesResponseDTO.setToggleDTOList(toggleDTOList);
        return togglesResponseDTO;
    }

    public void addToggleEnvironmentsToToggleDTO(ToggleDTO toggleDTO, Toggle toggle) {
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentService.fetchAllByToggle(toggle);

        List<ToggleEnvironmentDTO> dtos = toggleEnvironments.stream()
                .sorted(Comparator.comparingLong(te -> te.getEnvironment().getId()))
                .map(toggleEnvironmentConverter::toDTO)
                .collect(Collectors.toList());

        toggleDTO.setToggleEnvironmentDTOList(dtos);
    }

    private void addTagsToToggleDTO(ToggleDTO toggleDTO, Toggle toggle) {
        List<Tag> tags = toggleTagService.fetchAllTagsByToggle(toggle);
        List<TagDTO> tagDTOS = tags.stream()
                .map(TagDTO::toDTO)
                .collect(Collectors.toList());
        toggleDTO.setTagDTOList(tagDTOS);
    }


    public ToggleEnvironmentDTO addPayloadInToggleEnvironment(Long projectId,
                                                              Long instanceId,
                                                              Long toggleId,
                                                              Long environmentId,
                                                              String enabledValue,
                                                              String disabledValue) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        Instance instance = projectService.fetchInstanceByProjectIdAndInstanceId(projectId, instanceId);

        List<InstanceEnvironment> instanceEnvironments = instanceEnvironmentService.fetchAllByInstanceIdAndActiveTrue(instanceId);

        InstanceEnvironment instanceEnvironment = instanceEnvironments.stream()
                .filter(ie -> ie.getEnvironment().getId().equals(environmentId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("InstanceEnvironment not found with id: " + environmentId));

        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.addPayload(toggle, environmentId, instanceId, enabledValue, disabledValue);

        eventService.submitAction(ActionType.UPDATE, toggleEnvironment.getToggle().getProject(), toggleEnvironment.getToggle(), toggleEnvironment.getEnvironment());

        return toggleEnvironmentConverter.toDTO(toggleEnvironment);
    }


    public ToggleDTO makeProjectToggleDTOFromToggle(Toggle toggle){
        ToggleDTO toggleDTO = ToggleDTO.toDTO(toggle);

        addToggleEnvironmentsToToggleDTO(toggleDTO, toggle);
        addTagsToToggleDTO(toggleDTO, toggle);
        return toggleDTO;
    }

    public ToggleDTO makeToggleDTOFromToggle(Toggle toggle){
        ToggleDTO toggleDTO = ToggleDTO.toDTO(toggle);
        addTagsToToggleDTO(toggleDTO, toggle);
        return toggleDTO;
    }

    public ToggleDTO getToggleFromProject(Long projectId, Long toggleId){
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        return makeProjectToggleDTOFromToggle(toggle);
    }

    public void enableToggleInEnvironment(Long projectId, Long toggleId, String environmentName, Long instanceId) {

        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        Environment environment = environmentService.fetchEnvironmentByName(environmentName);
        Instance instance = instanceService.fetchInstance(instanceId);
        Project project = projectService.fetchProject(projectId);

        eventService.submitAction(ActionType.ENABLE,project,toggle,environment,instance);

        toggleEnvironmentService.enableByToggleIdEnvNameAndInstanceId(toggle.getId(), environmentName, instanceId);
    }

    public void disableToggleInEnvironment(Long projectId, Long toggleId, String environmentName, Long instanceId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        Environment environment = environmentService.fetchEnvironmentByName(environmentName);
        Instance instance = instanceService.fetchInstance(instanceId);
        Project project = projectService.fetchProject(projectId);

        eventService.submitAction(ActionType.DISABLE,project,toggle,environment,instance);

        toggleEnvironmentService.disableByToggleIdEnvNameAndInstanceId(toggle.getId(), environmentName, instanceId);
    }


    public void createToggleEnvironments(Long instanceId, Long environmentId) {
        Project project=projectService.fetchProjectByInstanceId(instanceId);
        List<Toggle> toggleList = fetchAllTogglesByProjectId(project.getId());

        toggleEnvironmentService.createEnvironmentsToggleAssociations(toggleList, environmentId, instanceId);
    }


    public ToggleEnvironment fetchToggleEnvironment( Long toggleId,Long instanceId, Long environmentId) {
        Toggle toggle = fetchToggleById(toggleId);
        return toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environmentId,instanceId);

    }

    public ToggleEnvironmentDTO getToggleEnvironment( Long toggleId,Long instanceId, Long environmentId) {
        ToggleEnvironment toggleEnvironment = fetchToggleEnvironment(toggleId, instanceId,environmentId);
        return toggleEnvironmentConverter.toDTO(toggleEnvironment);
    }


    public void addTagToToggle(Long tagId, Long toggleId, Long projectId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        toggleTagService.assignTagToToggle(toggle, tagId, projectId);
    }

    public Toggle updateToggle(Toggle toggle, Long toggleId, Long projectId) {
        return toggleRepository.findByIdAndProjectId(toggleId, projectId)
                .map(foundToggle -> {
                    foundToggle.setName(toggle.getName());
                    foundToggle.setDescription(toggle.getDescription());
                    foundToggle.setToggleType(toggle.getToggleType());
                    return toggleRepository.save(foundToggle);
                })
                .orElseGet(() -> saveToggle(toggle, projectId));
    }

    public ToggleEnvironmentDTO updatePayloadInToggleEnvironment(Long projectId, Long toggleId, Long environmentId, Long instanceId, String enabledValue, String disabledValue) {
        return addPayloadInToggleEnvironment(projectId, instanceId, toggleId, environmentId, enabledValue, disabledValue);
    }


    public void deleteToggle(Long id) {
        toggleRepository.deleteById(id);
    }
    public void deleteToggleByProject(Long projectId, Long toggleId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        toggleRepository.delete(toggle);
    }

    public void deleteToggleEnvironments(Long instanceId, Long envId) {
        Project project=projectService.fetchProjectByInstanceId(instanceId);
        toggleEnvironmentService.deleteEnvironmentToggleByProjectEnvAndInstanceId(project, envId, instanceId);
    }


    public void removeTagFromToggle(Long tagId, Long toggleId, Long projectId){
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        toggleTagService.deleteTagFromToggle(toggle, tagId, projectId);
    }


    public void removePayloadInToggleEnvironment(Long projectId, Long toggleId, Long environmentId, Long instanceId) {

        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        toggleEnvironmentService.removePayload(toggle, environmentId, instanceId);
    }


    @Scheduled(fixedRate = 60000)
    public void checkAndToggleFlags() {
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentService.findAllToggleEnvironments();
        System.out.println("Checking and toggling flags");

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        for (ToggleEnvironment toggleEnvironment : toggleEnvironments) {

            if(toggleEnvironment.getStartDate()!=null && toggleEnvironment.getStartOn()!=null){
                if ((toggleEnvironment.getStartDate().isBefore(today) || toggleEnvironment.getStartDate().isEqual(today)) && toggleEnvironment.getStartOn().isBefore(now) && !toggleEnvironment.getEnabled()){
                    toggleEnvironment.setStartDate(null);
                    toggleEnvironment.setStartOn(null);

                    toggleEnvironment.setEnabled(true);
                    toggleEnvironmentService.saveToggleEnvironment(toggleEnvironment);
                }
            }else if(toggleEnvironment.getStartDate()!=null){
                if((toggleEnvironment.getStartDate().isBefore(today)|| toggleEnvironment.getStartDate().isEqual(today)) && !toggleEnvironment.getEnabled()) {
                        toggleEnvironment.setStartDate(null);
                        toggleEnvironment.setEnabled(true);
                        toggleEnvironmentService.saveToggleEnvironment(toggleEnvironment);
                }
            }else if(toggleEnvironment.getStartOn()!=null){
                if(toggleEnvironment.getStartOn().isBefore(now) && !toggleEnvironment.getEnabled()){
                    toggleEnvironment.setStartOn(null);
                    toggleEnvironment.setEnabled(true);
                    toggleEnvironmentService.saveToggleEnvironment(toggleEnvironment);
                }
            }


            if(toggleEnvironment.getEndDate()!=null && toggleEnvironment.getStartOff()!=null){
                if((toggleEnvironment.getEndDate().isBefore(today) || toggleEnvironment.getEndDate().isEqual(today))&& toggleEnvironment.getStartOff().isBefore(now) && toggleEnvironment.getEnabled()){
                    toggleEnvironment.setEndDate(null);
                    toggleEnvironment.setStartOff(null);
                    toggleEnvironment.setEnabled(false);
                    toggleEnvironmentService.saveToggleEnvironment(toggleEnvironment);
                }
            }else if(toggleEnvironment.getEndDate()!=null){
                if((toggleEnvironment.getEndDate().isBefore(today)|| toggleEnvironment.getEndDate().isEqual(today)) && toggleEnvironment.getEnabled()) {
                    toggleEnvironment.setEndDate(null);
                    toggleEnvironment.setEnabled(false);
                    toggleEnvironmentService.saveToggleEnvironment(toggleEnvironment);
                }
            }else if(toggleEnvironment.getStartOff()!=null){
                if(toggleEnvironment.getStartOff().isBefore(now) && toggleEnvironment.getEnabled()){
                    toggleEnvironment.setStartOff(null);
                    toggleEnvironment.setEnabled(false);
                    toggleEnvironmentService.saveToggleEnvironment(toggleEnvironment);
                }
            }

        }
    }

    public List<ToggleScheduleDTO> getAllStrategiesForFlag( Long toggleId, Long instanceId) {
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentService.findAllByToggleIdAndInstanceId(toggleId, instanceId);

        return toggleEnvironments.stream()
                .map(toggleScheduleConverter::toDTO)
                .collect(Collectors.toList());
    }


    public Toggle fetchToggleById(Long toggleId) {
        return toggleRepository.findById(toggleId)
                .orElseThrow(() -> new NoSuchElementException("Toggle not found with id: " + toggleId));
    }


        public List<ConstraintDTO> getAllConstraintsForToggle(Long projectId, Long toggleId) {
            Toggle toggle = toggleRepository.findByIdAndProjectId(toggleId, projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Toggle not found"));

            return toggle.getConstraints().stream()
                    .map(constraint -> {
                        List<ConstraintValue> defaultValues = constraint.getValues().stream()
                                .filter(value -> value.getToggleEnvironment() == null)
                                .collect(Collectors.toList());

                        constraint.setValues(defaultValues);
                        return constraintConverter.toDTO(constraint);
                    })
                    .collect(Collectors.toList());
        }
    public List<ConstraintDTO> getAllConstraintsForInstanceEnvironment(Long toggleId, Long instanceId, Long environmentId) {
        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(toggleId, environmentId, instanceId);

        return constraintRepository.findAllByToggleId(toggleId).stream()
                .map(constraint -> {
                    List<ConstraintValue> specificValues = constraintValueRepository.findAllByConstraintIdAndToggleEnvironmentId(constraint.getId(), toggleEnvironment.getId());

                    if (!specificValues.isEmpty()) {
                        constraint.setValues(specificValues);
                    } else {
                        List<ConstraintValue> defaultValues = constraint.getValues().stream()
                                .filter(value -> value.getToggleEnvironment() == null)
                                .collect(Collectors.toList());
                        constraint.setValues(defaultValues);
                    }

                    return constraintConverter.toDTO(constraint);
                })
                .collect(Collectors.toList());
    }



    public void removeConstraintFromToggleEnvironment(Long projectId, Long toggleId, Long constraintId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        constraintService.deleteConstraintFromToggle(constraintId, toggle.getId());
    }
    public void removeAllConstraintsFromToggleEnvironment(Long projectId,
                                                          Long toggleId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        constraintService.deleteAllConstraintsFromToggle(toggle);
    }
    public void resetConstraintValuesToDefault(Long toggleId, Long environmentId, Long instanceId, Long constraintId) {
       constraintService.resetConstraintValuesToDefault(toggleId,environmentId,instanceId,constraintId);
    }


    public ConstraintDTO updateConstraintInToggle(Constraint constraint, Long projectId, Long toggleId, Long constraintId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        Constraint updatedConstraint = constraintService.updateConstraintInToggle(constraint, toggle, constraintId);

        return constraintConverter.toDTO(updatedConstraint);
    }
    public ConstraintDTO updateConstraintValuesForToggleEnvironment(
            Long projectId, Long toggleId, Long instanceId, Long environmentId, Long constraintId, ConstraintValueUpdateDTO newValues) {

        constraintService.updateConstraintValuesForToggleEnvironment(toggleId, environmentId, instanceId, constraintId, newValues);
        Constraint updatedConstraint = constraintService.fetchConstraintFromToggleEnv(constraintId, toggleId, instanceId, environmentId);

        return constraintConverter.toDTO(updatedConstraint);
    }


    public ConstraintDTO getConstraintFromToggle(Long projectId, Long toggleId, Long constraintId) {
        Constraint constraint = constraintService.fetchConstraintFromToggle(constraintId, toggleId);
        return constraintConverter.toDTO(constraint);
    }
    public ConstraintDTO getConstraintFromToggleEnvironment(Long constraintId, Long toggleId, Long instanceId, Long environmentId){
        Constraint constraint=constraintService.fetchConstraintFromToggleEnv(constraintId, toggleId, instanceId, environmentId);
        return constraintConverter.toDTO(constraint);

    }

    public ConstraintDTO addConstraintInToggle(Constraint constraint, Long projectId, Long toggleId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        Constraint addedConstrain = constraintService.createConstraintInToggle(constraint, toggle);

        return constraintConverter.toDTO(addedConstrain);
    }


    public Boolean evaluateToggleInContext(
            String toggleName,
            String apiTokenStr,
            List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> contextFields) {

        if(apiTokenStr.contains("Bearer")){
            apiTokenStr = apiTokenStr.replace("Bearer ", "");
        }

        String[] parts = apiTokenStr.split(":");


        String projectId =parts[0];
        String instanceId = parts[1];
        String environmentId = parts[2];

        String[] subParts = parts[3].split("\\.");
        Integer toggleType = Integer.parseInt(subParts[0]);

        Project project = projectRepository.findByHashId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        Instance instance = instanceRepository.findByHashId(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Instance not found with id: " + instanceId));
        Environment environment=environmentRepository.findByHashId(environmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + environmentId));


        Toggle toggle = toggleRepository.findByNameAndProjectAndToggleType(toggleName, project, toggleType)
                .orElseThrow(() -> new ToggleNotFoundException(toggleName, project.getId()));

        Boolean enabled = toggleEnvironmentService.evaluateToggleInContext(toggle, environment, instance.getId(), contextFields);
        System.out.println("enabled "+toggleName+" "+enabled);
       return enabled;
    }

    public List<ConstraintValueDTO> getConstraintValues(Long constraintId) {
        Constraint constraint = constraintService.fetchConstraint(constraintId);
        List<ConstraintValue> constraintValues = constraintValueRepository.findAllByConstraintId(constraintId);
        return constraintValues.stream()
                .map(ConstraintValueConverter::toDTO)
                .collect(Collectors.toList());
    }


    public List<ConstraintDTO> getConstraints(String apiTokenStr, String toggleName){
        if(apiTokenStr.contains("Bearer")){
            apiTokenStr = apiTokenStr.replace("Bearer ", "");
        }
        String[] parts =apiTokenStr.split(":");

        String projectId = parts[0];
        String instanceId =parts[1];
        String environmentId = parts[2];

        Long projectIdLong = projectRepository.findByHashId(projectId).orElseThrow().getId();
        Long environmentIdLong = environmentRepository.findByHashId(environmentId).orElseThrow().getId();
        Long instanceIdLong = instanceRepository.findByHashId(instanceId).orElseThrow().getId();

        System.out.println("ProjectId in constraint client: "+projectIdLong);

        List<Toggle> toggles=fetchAllTogglesByProjectId(projectIdLong);
        System.out.println("Toggles: "+toggles.get(0).getName());
        System.out.println("ToggleName: "+toggleName);
        Toggle targetToggle = toggles.stream()
                .filter(toggle -> toggle.getName().equals(toggleName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Toggle not found with name: " + toggleName));

        ToggleEnvironment toggleEnvironment= toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(targetToggle.getId(),environmentIdLong, instanceIdLong);


        List<ConstraintDTO> filteredConstraints = targetToggle.getConstraints().stream().map(constraint -> {
            List<ConstraintValue> specificValues = constraint.getValues().stream()
                    .filter(cv -> cv.getToggleEnvironment() != null && toggleEnvironment.getId().equals(cv.getToggleEnvironment().getId()))
                    .toList();

            List<ConstraintValue> defaultValues = constraint.getValues().stream()
                    .filter(cv -> cv.getToggleEnvironment() == null)
                    .toList();

            List<ConstraintValue> selectedValues = !specificValues.isEmpty() ? specificValues : defaultValues;

            constraint.setValues(selectedValues);
            return ConstraintDTO.toDTO(constraint);
        }).toList();

        return filteredConstraints;
    }

    public ClientToggleEvaluationResponseDTO evaluateToggleInContextZKP( String toggleName, String apiTokenStr, JsonNode proof) {
        ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = new ClientToggleEvaluationResponseDTO();

        if(apiTokenStr.contains("Bearer")){
            apiTokenStr = apiTokenStr.replace("Bearer ", "");
        }
        String[] parts =apiTokenStr.split(":");

        String projectId = parts[0];
        String instanceId =parts[1];
        String environmentId = parts[2];

        Long projectIdLong = projectRepository.findByHashId(projectId).orElseThrow().getId();
        Long environmentIdLong = environmentRepository.findByHashId(environmentId).orElseThrow().getId();
        Long instanceIdLong = instanceRepository.findByHashId(instanceId).orElseThrow().getId();
        Environment environment = environmentRepository.findByHashId(environmentId).orElseThrow();

        List<Toggle> toggles=fetchAllTogglesByProjectId(projectIdLong);
        Toggle targetToggle = toggles.stream()
                .filter(toggle -> toggle.getName().equals(toggleName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Toggle not found with name: " + toggleName));

        ToggleEnvironment toggleEnvironment= toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(targetToggle.getId(),environmentIdLong, instanceIdLong);

        if(!toggleEnvironment.getEnabled()){
            clientToggleEvaluationResponseDTO.setEnabled(false);
            String payload = toggleEnvironmentService.getPayloadInToggleEnv(targetToggle, environment, instanceIdLong, false);
            clientToggleEvaluationResponseDTO.setPayload(payload);
            return clientToggleEvaluationResponseDTO;
        }


        ZKPVerifier zkpVerifier = new ZKPVerifier();
        boolean isValid = false;
        String publicValue = "0";

        try {
            isValid = zkpVerifier.verifyProof(proof);

            if (isValid) {
                JsonNode publicSignalsNode = proof.get("publicSignals");
                String publicSignalsValue = publicSignalsNode.get(0).asText();
                publicValue = publicSignalsValue;

            }
        } catch (Exception e) {
            System.err.println("Eroare la verificare: " + e.getMessage());
            e.printStackTrace();
        }

        String payload = toggleEnvironmentService.getPayloadInToggleEnv(targetToggle, environment, instanceIdLong, publicValue.equals("1"));

        clientToggleEvaluationResponseDTO.setPayload(payload);
        clientToggleEvaluationResponseDTO.setEnabled(publicValue.equals("1"));

        return clientToggleEvaluationResponseDTO;
    }


    public Boolean evaluateProofs(
            String toggleName,
            String apiTokenStr,
            List<JsonNode> proofs) {

        if (apiTokenStr.contains("Bearer")) {
            apiTokenStr = apiTokenStr.replace("Bearer ", "");
        }
        String[] parts = apiTokenStr.split(":");
        String projectId = parts[0];
        String instanceId = parts[1];
        String environmentId = parts[2];

        Long projectIdLong = projectRepository.findByHashId(projectId).orElseThrow().getId();
        Long environmentIdLong = environmentRepository.findByHashId(environmentId).orElseThrow().getId();
        Long instanceIdLong = instanceRepository.findByHashId(instanceId).orElseThrow().getId();
        Environment environment = environmentRepository.findByHashId(environmentId).orElseThrow();

        List<Toggle> toggles = fetchAllTogglesByProjectId(projectIdLong);
        Toggle targetToggle = toggles.stream()
                .filter(toggle -> toggle.getName().equals(toggleName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Toggle not found: " + toggleName));

        List<Constraint> confidentialConstraints = targetToggle.getConstraints().stream()
                .filter(c -> c.getIsConfidential() != null && c.getIsConfidential() == 1)
                .collect(Collectors.toList());

        if ( proofs.size() != confidentialConstraints.size()) {
            System.out.println("Proofs count mismatch. Expected: " + confidentialConstraints.size() +
                    ", Received: " + (proofs != null ? proofs.size() : 0));
            return false;
        }

        Set<String> proofHashes = new HashSet<>();

        for (JsonNode proof : proofs) {
            try {
                String proofHash = proof.hashCode() + proof.toString();
                if (proofHashes.contains(proofHash)) {
                    System.out.println("Duplicate proof detected for context" );
                    return false;
                }
                proofHashes.add(proofHash);

            } catch (Exception e) {
                System.out.println("Invalid proof format: " + e.getMessage());
                return false;
            }
        }

        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(
                targetToggle.getId(), environmentIdLong, instanceIdLong);

        if (!toggleEnvironment.getEnabled()) {
            return false;
        }

        ZKPVerifier zkpVerifier = new ZKPVerifier();
        List<Boolean> proofResults = new ArrayList<>();
        List<String> publicValues = new ArrayList<>();

        for (JsonNode proof : proofs) {
            try {
                boolean isValid = zkpVerifier.verifyProof(proof);
                proofResults.add(isValid);

                if (isValid) {
                    JsonNode publicSignalsNode = proof.get("publicSignals");
                    publicValues.add(publicSignalsNode.get(0).asText());
                    System.out.println("valoare public signals: " + publicSignalsNode.get(0).asText());
                } else {
                    publicValues.add("0");
                }
            } catch (Exception e) {
                System.err.println("Error verifying proof: " + e.getMessage());
                proofResults.add(false);
                publicValues.add("0");
            }
        }

        boolean allProofsValid = proofResults.stream().allMatch(Boolean::booleanValue);
        boolean finalEnabled;
        if (allProofsValid) {
            finalEnabled = publicValues.stream().allMatch("1"::equals);
        } else {
            finalEnabled = false;
        }

        return finalEnabled;
    }


    public String getPayload(String toggleName,String apiTokenStr, Boolean enable) {
        if (apiTokenStr.contains("Bearer")) {
            apiTokenStr = apiTokenStr.replace("Bearer ", "");
        }
        String[] parts = apiTokenStr.split(":");
        String projectId = parts[0];
        String instanceId = parts[1];
        String environmentId = parts[2];

        Long projectIdLong = projectRepository.findByHashId(projectId).orElseThrow().getId();
        Long environmentIdLong = environmentRepository.findByHashId(environmentId).orElseThrow().getId();
        Long instanceIdLong = instanceRepository.findByHashId(instanceId).orElseThrow().getId();
        Environment environment = environmentRepository.findByHashId(environmentId).orElseThrow();


        List<Toggle> toggles = fetchAllTogglesByProjectId(projectIdLong);
        Toggle targetToggle = toggles.stream()
                .filter(toggle -> toggle.getName().equals(toggleName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Toggle not found with name: " + toggleName));



        String payload = toggleEnvironmentService.getPayloadInToggleEnv(targetToggle, environment, instanceIdLong, enable);
        return payload;
    }
}
