package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.exceptions.EnvironmentNotFoundException;
import ro.mta.toggleserverapi.exceptions.ToggleEnvNotFoundException;
import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
import ro.mta.toggleserverapi.repositories.ToggleEnvironmentRepository;
import ro.mta.toggleserverapi.repositories.ToggleRepository;
import ro.mta.toggleserverapi.util.ConstraintUtil;

import java.time.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ToggleEnvironmentService {
    private final ToggleEnvironmentRepository toggleEnvironmentRepository;
    private final EnvironmentRepository environmentRepository;
    private final EventService eventService;
    private final InstanceRepository instanceRepository;


    public Environment fetchEnvironmentById(Long environmentId){
        return environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException(environmentId));
    }

    public List<ToggleEnvironment> fetchAllByToggle(Toggle toggle){
        return toggleEnvironmentRepository.findAllByToggle(toggle);
    }

    public ToggleEnvironment fetchByToggleIdAndEnvIdAndInstanceId(Long toggleId, Long envId, Long instanceId) {
        return toggleEnvironmentRepository.findByToggleIdAndEnvIdAndInstanceId(toggleId, envId, instanceId)
                .orElseThrow(() -> new ToggleEnvNotFoundException(toggleId, envId, instanceId));
    }

    public Boolean fetchByToggleAndEnvIdAndInstanceId(Toggle toggle, Long environmentId, Long instanceId) {
        Environment environment = fetchEnvironmentById(environmentId);
        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new NoSuchElementException("Instance not found with id: " + instanceId));
       ToggleEnvironment toggle_env=   toggleEnvironmentRepository.findByToggleAndEnvironmentAndInstance(toggle, environment, instance);
       if(toggle_env!=null)
           return toggle_env.getEnabled();
         return false;

    }


    @Transactional
    public void enableByToggleIdEnvNameAndInstanceId(Long toggleId, String environmentName, Long instanceId) {
        ToggleEnvironment toggleEnvironment = fetchByToggleIdEnvNameAndInstanceId(toggleId, environmentName, instanceId);
        toggleEnvironment.setEnabled(Boolean.TRUE);

        toggleEnvironmentRepository.save(toggleEnvironment);
    }

    public ToggleEnvironment fetchByToggleIdEnvNameAndInstanceId(Long toggleId, String environmentName, Long instanceId) {
        return toggleEnvironmentRepository.findByToggleIdEnvNameAndInstanceId(toggleId, environmentName, instanceId)
                .orElseThrow(() -> new ToggleEnvNotFoundException(toggleId, environmentName, instanceId));
    }


    @Transactional
    public ToggleEnvironment addPayload(Toggle toggle,
                                        Long environmentId,
                                        Long instanceId,
                                        String enabledValue,
                                        String disabledValue) {
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environmentId, instanceId);

        toggleEnvironment.setEnabledValue(enabledValue);
        toggleEnvironment.setDisabledValue(disabledValue);

        return toggleEnvironmentRepository.save(toggleEnvironment);
    }

    @Transactional
    public void disableByToggleIdEnvNameAndInstanceId(Long toggleId, String environmentName, Long instanceId) {
        ToggleEnvironment toggleEnvironment = toggleEnvironmentRepository.findByToggleIdEnvNameAndInstanceId(toggleId, environmentName, instanceId)
                .orElseThrow(() -> new NoSuchElementException("ToggleEnvironment not found for given toggle, environment, and instance."));

        toggleEnvironment.setEnabled(false);
        toggleEnvironmentRepository.save(toggleEnvironment);

    }


    public Long getEnabledTogglesCountInEnvironmentAndInstance(Environment environment, Instance instance) {
        return toggleEnvironmentRepository.findAllByEnvironmentAndInstanceAndEnabledTrue(environment, instance)
                .stream()
                .count();
    }


    public Boolean evaluateToggleInContext(Toggle toggle,
                                           Environment environment,
                                           Long instanceId,
                                           List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> contextFields) {
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environment.getId(), instanceId);

        if (toggleEnvironment != null && toggleEnvironment.getEnabled()) {
            List<Constraint> filteredConstraints = toggle.getConstraints().stream().map(constraint -> {
                List<ConstraintValue> specificValues = constraint.getValues().stream()
                        .filter(cv -> cv.getToggleEnvironment() != null && toggleEnvironment.getId().equals(cv.getToggleEnvironment().getId()))
                        .toList();

                List<ConstraintValue> defaultValues = constraint.getValues().stream()
                        .filter(cv -> cv.getToggleEnvironment() == null)
                        .toList();

                List<ConstraintValue> selectedValues = !specificValues.isEmpty() ? specificValues : defaultValues;

                constraint.setValues(selectedValues);
                return constraint;
            }).toList();

            return ConstraintUtil.validate(filteredConstraints, contextFields);
        } else {
            return Boolean.FALSE;
        }
    }



    public String getPayloadInToggleEnv(Toggle toggle, Environment environment, Long instanceId, Boolean resultEnabled) {
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environment.getId(), instanceId);
        if (resultEnabled) {
            return toggleEnvironment.getEnabledValue();
        } else {
            return toggleEnvironment.getDisabledValue();
        }
    }

    @Transactional
    public void removePayload(Toggle toggle, Long environmentId, Long instanceId) {
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environmentId, instanceId);
        toggleEnvironment.setEnabledValue(null);
        toggleEnvironment.setDisabledValue(null);
        toggleEnvironmentRepository.save(toggleEnvironment);
    }


    public List<ToggleEnvironment> findAllToggleEnvironments() {
        return toggleEnvironmentRepository.findAll();
    }

    public ToggleEnvironment saveToggleEnvironment(ToggleEnvironment toggleEnvironment) {
        return toggleEnvironmentRepository.save(toggleEnvironment);
    }

    public ToggleEnvironment setToggleSchedule(Long toggleId, Long environmentId, Long instanceId,
                                               LocalTime startOn, LocalTime startOff,
                                               LocalDate startDate, LocalDate endDate,
                                               ZoneId userZone) {
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggleId, environmentId, instanceId);

        LocalTime startOnUtc = (startOn != null)
                ? startOn.atDate(LocalDate.now()).atZone(userZone).toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalTime()
                : null;

        LocalTime startOffUtc = (startOff != null)
                ? startOff.atDate(LocalDate.now()).atZone(userZone).toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalTime()
                : null;

        toggleEnvironment.setStartOn(startOnUtc);
        toggleEnvironment.setStartOff(startOffUtc);
        toggleEnvironment.setStartDate(startDate);
        toggleEnvironment.setEndDate(endDate);

        return toggleEnvironmentRepository.save(toggleEnvironment);
    }


    public void createToggleEnvironmentAssociation(Toggle toggle, Environment environment, Instance instance) {
        ToggleEnvironment existingAssociation = toggleEnvironmentRepository.findByToggleAndEnvironmentAndInstance(toggle, environment, instance);

        if (existingAssociation==null) {
            ToggleEnvironment toggleEnvironment = new ToggleEnvironment();
            toggleEnvironment.setToggle(toggle);
            toggleEnvironment.setEnvironment(environment);
            toggleEnvironment.setInstance(instance);
            toggleEnvironment.setEnabled(false);
            toggleEnvironmentRepository.save(toggleEnvironment);
        }
    }

    public void createEnvironmentsToggleAssociations(List<Toggle> toggleList, Long environmentId, Long instanceId) {
        for (Toggle toggle : toggleList) {
            ToggleEnvironment toggleEnvironment = new ToggleEnvironment();
            toggleEnvironment.setToggle(toggle);
            toggleEnvironment.setEnvironment(environmentRepository.findById(environmentId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found with id: " + environmentId)));
            toggleEnvironment.setInstance(instanceRepository.findById(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found with id: " + instanceId)));

            toggleEnvironment.setEnabled(false);
            toggleEnvironmentRepository.save(toggleEnvironment);
        }
    }

    public void deleteEnvironmentToggleByProjectEnvAndInstanceId(Project project, Long envId, Long instanceId) {
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentRepository.findByProjectAndEnvironmentAndInstance(project, envId, instanceId);
        toggleEnvironmentRepository.deleteAll(toggleEnvironments);
    }

    public List<ToggleEnvironment> findAllByToggleIdAndInstanceId(Long toggleId, Long instanceId) {
        return toggleEnvironmentRepository.findAllByToggleIdAndInstanceId(toggleId, instanceId);
    }

    public void deleteByEnvironmentId(Long environmentId) {
        toggleEnvironmentRepository.deleteByEnvironmentId(environmentId);
    }

}
