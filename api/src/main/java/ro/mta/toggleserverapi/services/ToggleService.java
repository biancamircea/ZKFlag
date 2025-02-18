package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.ClientFeaturesConverter;
import ro.mta.toggleserverapi.converters.ConstraintConverter;
import ro.mta.toggleserverapi.converters.ToggleEnvironmentConverter;
import ro.mta.toggleserverapi.converters.ToggleScheduleConverter;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.exceptions.ResourceNotFoundException;
import ro.mta.toggleserverapi.exceptions.ToggleNotFoundException;
import ro.mta.toggleserverapi.repositories.ToggleRepository;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
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
        // Obține proiectul pe baza ID-ului
        Project project = projectService.fetchProject(projectId);
        toggle.setProject(project);

        // Salvează toggle-ul în baza de date
        Toggle savedToggle = toggleRepository.save(toggle);

        // Generează un eveniment pentru crearea toggle-ului
        eventService.submitAction(ActionType.CREATE, project, savedToggle);

        // Obține toate instanțele proiectului
        List<Instance> instances = instanceService.fetchInstancesByProject(projectId);

        // Pentru fiecare instanță, obține mediile active și creează legături în tabela `ToggleEnvironment`
        for (Instance instance : instances) {
            List<Environment> activeEnvironments = instanceService.fetchEnabledEnvironmentsInInstance(instance);

            for (Environment environment : activeEnvironments) {
                // Creează legătura între toggle, mediu și instanță
                toggleEnvironmentService.createToggleEnvironmentAssociation(savedToggle, environment, instance);
            }
        }

        return savedToggle;
    }

    public TogglesResponseDTO getAllToggles(){
//        get the toggles
        List<Toggle> toggleList = fetchAllToggles();
//        convert them in ToggleDTO list
        List<ToggleDTO> toggleDTOList = new ArrayList<>();
        for(Toggle toggle : toggleList){
            ToggleDTO toggleDTO = makeToggleDTOFromToggle(toggle);
            toggleDTOList.add(toggleDTO);
        }
        return TogglesResponseDTO.toDTOSimple(toggleDTOList);
    }

    public TogglesResponseDTO getAllTogglesFromProject(Long projectId){
//        get the toggles from the project
        List<Toggle> toggleList = fetchAllTogglesByProjectId(projectId);
//        convert them in ToggleDTO list
        List<ToggleDTO> toggleDTOList = new ArrayList<>();
        for(Toggle toggle : toggleList){
            ToggleDTO toggleDTO = makeProjectToggleDTOFromToggle(toggle);
            toggleDTOList.add(toggleDTO);
        }
//        create ToggleResponseDTO
        TogglesResponseDTO togglesResponseDTO = new TogglesResponseDTO();
//        set "toggles" list
        togglesResponseDTO.setToggleDTOList(toggleDTOList);
        return togglesResponseDTO;
    }

    public void addToggleEnvironmentsToToggleDTO(ToggleDTO toggleDTO, Toggle toggle) {
//        get toggle-env links for the toggle
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentService.fetchAllByToggle(toggle);

//        transform links in DTO objects
        List<ToggleEnvironmentDTO> dtos = toggleEnvironments.stream()
                .sorted(Comparator.comparingLong(te -> te.getEnvironment().getId()))
                .map(toggleEnvironmentConverter::toDTO)
                .collect(Collectors.toList());

//        create ToggleDTO object
        toggleDTO.setToggleEnvironmentDTOList(dtos);
    }

    private void addTagsToToggleDTO(ToggleDTO toggleDTO, Toggle toggle) {
//        get all the toggle-tag links
        List<Tag> tags = toggleTagService.fetchAllTagsByToggle(toggle);
//        transform list of tags in list of tagsDTO
        List<TagDTO> tagDTOS = tags.stream()
                .map(TagDTO::toDTO)
                .collect(Collectors.toList());
        toggleDTO.setTagDTOList(tagDTOS);
    }

    public ConstraintDTO addConstraintInToggle(Constraint constraint, Long projectId, Long toggleId) {
//        make sure toggle exists in project
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
//        get toggle-env link
        Constraint addedConstrain = constraintService.createConstraintInToggle(constraint, toggle);

        return constraintConverter.toDTO(addedConstrain);
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
//        create DTO with static fields
        ToggleDTO toggleDTO = ToggleDTO.toDTO(toggle);

//        add dynamic fields
//        add toggle-env links
        addToggleEnvironmentsToToggleDTO(toggleDTO, toggle);
//        add toggle-tag links
        addTagsToToggleDTO(toggleDTO, toggle);
        return toggleDTO;
    }

    public ToggleDTO makeToggleDTOFromToggle(Toggle toggle){
//        create DTO with static fields
        ToggleDTO toggleDTO = ToggleDTO.toDTO(toggle);

//        add dynamic fields
//        add toggle-tag links
        addTagsToToggleDTO(toggleDTO, toggle);
        return toggleDTO;
    }

    public ToggleDTO getToggleFromProject(Long projectId, Long toggleId){
//        fetch toggle
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        return makeProjectToggleDTOFromToggle(toggle);
    }

    public ConstraintDTO getConstraintFromToggle(Long projectId, Long toggleId, Long constraintId) {
        //        make sure toggle exists in project
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
//
        Constraint constraint = constraintService.fetchConstraintFromToggle(constraintId, toggle.getId());
        return constraintConverter.toDTO(constraint);
    }

    public ClientFeaturesDTO getClientFeatures(Long envId, Long projectId, Long instanceId) {
        // Obține lista de toggles pentru proiectul specific
        List<Toggle> toggleList = fetchAllTogglesByProjectId(projectId);

        // Inițializează lista de ToggleEnvironment
        List<ToggleEnvironment> toggleEnvironments = new ArrayList<>();

        // Pentru fiecare toggle, obține relația Toggle-Environment-Instance
        for (Toggle toggle : toggleList) {
            ToggleEnvironment toggleEnvironment = toggleEnvironmentService
                    .fetchByToggleEnvAndInstanceId(toggle, envId, instanceId); // Metodă actualizată
            if (toggleEnvironment != null) {
                toggleEnvironments.add(toggleEnvironment);
            }
        }

        // Convertește lista de ToggleEnvironment în DTO-ul clientului
        return ClientFeaturesConverter.toDTO(toggleEnvironments);
    }


    public ClientToggleEvaluationResponseDTO evaluateToggleInContext(
            String toggleName,
            String apiTokenStr,
            List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> contextFields) {

        String[] parts = apiTokenStr.split(":");

        Long projectId = Long.parseLong(parts[0]);
        Long instanceId = Long.parseLong(parts[1]);
        String[] subParts = parts[2].split("\\.");
        Long environmentId = Long.parseLong(subParts[0]);

        Project project = projectService.fetchProject(projectId);
        Instance instance = instanceService.fetchInstance(instanceId);
        Environment environment= environmentService.fetchEnvironment(environmentId);

        Toggle toggle = toggleRepository.findByNameAndProject(toggleName, project)
                .orElseThrow(() -> new ToggleNotFoundException(toggleName, project.getId()));

        Boolean enabled = toggleEnvironmentService.evaluateToggleInContext(toggle, environment, instanceId, contextFields);

        String payload = toggleEnvironmentService.getPayloadInToggleEnv(toggle, environment, instanceId, enabled);

        ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = new ClientToggleEvaluationResponseDTO();
        clientToggleEvaluationResponseDTO.setEnabled(enabled);
        clientToggleEvaluationResponseDTO.setPayload(payload);
        return clientToggleEvaluationResponseDTO;
    }


    public List<Toggle> fetchTogglesByEnvironmentIdAndProjectIdAndInstanceId(Long envId, Long projectId, Long instanceId) {
        List<Toggle> toggleList = fetchAllTogglesByProjectId(projectId);
        List<Toggle> activeToggles = new ArrayList<>();

        for (Toggle toggle : toggleList) {
            for (ToggleEnvironment toggleEnvironment : toggle.getToggleEnvironmentList()) {
                if (toggleEnvironment.getEnvironment().getId().equals(envId) &&
                        toggleEnvironment.getInstance().getId().equals(instanceId) &&
                        toggleEnvironment.getEnabled()) {
                    activeToggles.add(toggle);
                }
            }
        }

        return activeToggles;
    }


    public Project fetchToggleProject(Long id) {
        return toggleRepository.findById(id).get().getProject();
    }


    public void enableToggleInEnvironment(Long projectId, Long toggleId, String environmentName, Long instanceId) {
        // Fetch toggle-ul din proiectul specific
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);

        // Activează toggle-ul în relația toggle-environment-instance
        toggleEnvironmentService.enableByToggleIdEnvNameAndInstanceId(toggle.getId(), environmentName, instanceId);
    }

    public void disableToggleInEnvironment(Long projectId, Long toggleId, String environmentName, Long instanceId) {
        // Fetch toggle-ul din proiectul specific
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);

        // Dezactivează toggle-ul în relația toggle-environment-instance
        toggleEnvironmentService.disableByToggleIdEnvNameAndInstanceId(toggle.getId(), environmentName, instanceId);
    }


    public void createToggleEnvironments(Long instanceId, Long environmentId) {
        Project project=projectService.fetchProjectByInstanceId(instanceId);
        // Obține toate toggle-urile din proiectul specific
        //print project

        List<Toggle> toggleList = fetchAllTogglesByProjectId(project.getId());
        System.out.println("Project: "+project+" ToggleList: "+toggleList+" EnvironmentId: "+environmentId+" InstanceId: "+instanceId);


        // Creează legături toggle-environment-instance
        toggleEnvironmentService.createEnvironmentsToggleAssociations(toggleList, environmentId, instanceId);
    }


    public ToggleEnvironment fetchToggleEnvironment(Long projectId, Long toggleId,Long instanceId, Long environmentId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        return toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(),instanceId, environmentId);

    }

    public ToggleEnvironmentDTO getToggleEnvironment(Long projectId, Long toggleId,Long instanceId, Long environmentId) {
        ToggleEnvironment toggleEnvironment = fetchToggleEnvironment(projectId, toggleId, instanceId,environmentId);
        return toggleEnvironmentConverter.toDTO(toggleEnvironment);
    }

//    receives a Toggle and creates a complete ToggleDTO response

    public void addTagToToggle(Long tagId, Long toggleId, Long projectId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        toggleTagService.assignTagToToggle(toggle, tagId, projectId);
    }

    public Toggle updateToggle(Toggle toggle, Long toggleId, Long projectId) {
        return toggleRepository.findByIdAndProjectId(toggleId, projectId)
                .map(foundToggle -> {
                    foundToggle.setName(toggle.getName());
                    foundToggle.setDescription(toggle.getDescription());
                    return toggleRepository.save(foundToggle);
                })
                .orElseGet(() -> saveToggle(toggle, projectId));
    }
    public Toggle updateToggleProject(Project project, Long id) {
        return toggleRepository
                .findById(id).map(toggle -> {
                    toggle.setProject(project);
                    return toggleRepository.save(toggle);
                })
                .orElseThrow(() -> new ToggleNotFoundException(id));

    }
    public ConstraintDTO updateConstraintInToggleEnv(Constraint constraint, Long projectId, Long toggleId, Long constraintId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        Constraint updatedConstraint = constraintService.updateConstraintInToggle(constraint, toggle, constraintId);

        return constraintConverter.toDTO(updatedConstraint);
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

    public void removeConstraintFromToggleEnvironment(Long projectId, Long toggleId, Long constraintId) {
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        constraintService.deleteConstraintFromToggle(constraintId, toggle.getId());
    }

    public void removeAllConstraintsFromToggleEnvironment(Long projectId,
                                                          Long toggleId) {
//        make sure toggle exists in project
        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
//        delete constraint
        constraintService.deleteAllConstraintsFromToggle(toggle);
    }

    public void removePayloadInToggleEnvironment(Long projectId, Long toggleId, Long environmentId, Long instanceId) {

        Toggle toggle = fetchToggleByProjectIdAndToggleId(projectId, toggleId);
        toggleEnvironmentService.removePayload(toggle, environmentId, instanceId);
    }


    @Scheduled(fixedRate = 60000) // Runs every minute
    public void checkAndToggleFlags() {
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentService.findAllToggleEnvironments();

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
                    System.out.println("Disabling toggle");
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

        // Transformă ToggleEnvironment în DTO-uri
        return toggleEnvironments.stream()
                .map(toggleScheduleConverter::toDTO)
                .collect(Collectors.toList());
    }



    public Toggle fetchToggleById(Long toggleId) {
        return toggleRepository.findById(toggleId)
                .orElseThrow(() -> new NoSuchElementException("Toggle not found with id: " + toggleId));
    }

    public List<ConstraintDTO> getAllConstraintsFromToggle(Long projectId, Long toggleId) {
        Toggle toggle = toggleRepository.findByIdAndProjectId( toggleId,projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Toggle not found"));
        return toggle.getConstraints().stream()
                .map(constraint -> constraintConverter.toDTO(constraint))
                .collect(Collectors.toList());
    }

}
