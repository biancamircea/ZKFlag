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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ToggleEnvironmentService {
    private final ToggleEnvironmentRepository toggleEnvironmentRepository;
    private final ToggleRepository toggleRepository;
    private final EnvironmentRepository environmentRepository;
    private final EventService eventService;
    private final InstanceRepository instanceRepository;

    public void createToggleEnvironment(Toggle toggle, Environment environment, Instance instance) {
        ToggleEnvironment toggleEnvironment = new ToggleEnvironment();
        toggleEnvironment.setEnvironment(environment);
        toggleEnvironment.setToggle(toggle);
        toggleEnvironment.setInstance(instance); // Setează instanța
        toggleEnvironmentRepository.save(toggleEnvironment); // Salvează în baza de date
    }


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


    public ToggleEnvironment fetchByToggleIdAndEnvNameAndInstanceId(Long toggleId, String envName, Long instanceId) {
        return toggleEnvironmentRepository.findByToggleIdAndEnvironmentNameAndInstanceId(toggleId, envName, instanceId)
                .orElseThrow(() -> new ToggleEnvNotFoundException(toggleId, envName, instanceId));
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
    public void enableByToggleIdAndEnvIdAndInstanceId(Long toggleId, Long environmentId, Long instanceId) {
        // Găsește ToggleEnvironment folosind toggleId, environmentId și instanceId
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggleId, environmentId, instanceId);
        toggleEnvironment.setEnabled(Boolean.TRUE);
        toggleEnvironmentRepository.save(toggleEnvironment); // Salvează modificarea în baza de date
    }


    @Transactional
    public void enableByToggleIdEnvNameAndInstanceId(Long toggleId, String environmentName, Long instanceId) {
        // Găsește ToggleEnvironment folosind toggleId, environmentName și instanceId
        ToggleEnvironment toggleEnvironment = fetchByToggleIdEnvNameAndInstanceId(toggleId, environmentName, instanceId);
        toggleEnvironment.setEnabled(Boolean.TRUE);

        // Salvează modificarea
        toggleEnvironmentRepository.save(toggleEnvironment);

        // Generează un eveniment pentru activare
        eventService.submitAction(
                ActionType.ENABLE,
                toggleEnvironment.getToggle().getProject(),
                toggleEnvironment.getToggle(),
                toggleEnvironment.getEnvironment()
        );
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
        // Fetch ToggleEnvironment folosind toggle, environment și instance
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environmentId, instanceId);

        // Setează valorile pentru payload
        toggleEnvironment.setEnabledValue(enabledValue);
        toggleEnvironment.setDisabledValue(disabledValue);

        // Salvează modificările în repository
        return toggleEnvironmentRepository.save(toggleEnvironment);
    }


    @Transactional
    public void disableByToggleIdEnvIdAndInstanceId(Long toggleId, Long environmentId, Long instanceId) {
        // Găsește ToggleEnvironment folosind toggleId, environmentId și instanceId
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggleId, environmentId, instanceId);
        toggleEnvironment.setEnabled(Boolean.FALSE);

        // Salvează modificarea
        toggleEnvironmentRepository.save(toggleEnvironment);

        // Generează un eveniment pentru dezactivare
        eventService.submitAction(
                ActionType.DISABLE,
                toggleEnvironment.getToggle().getProject(),
                toggleEnvironment.getToggle(),
                toggleEnvironment.getEnvironment()
        );
    }

    @Transactional
    public void disableByToggleIdEnvNameAndInstanceId(Long toggleId, String environmentName, Long instanceId) {
        ToggleEnvironment toggleEnvironment = toggleEnvironmentRepository.findByToggleIdEnvNameAndInstanceId(toggleId, environmentName, instanceId)
                .orElseThrow(() -> new NoSuchElementException("ToggleEnvironment not found for given toggle, environment, and instance."));

        toggleEnvironment.setEnabled(false); // Dezactivează toggle-ul
        toggleEnvironmentRepository.save(toggleEnvironment); // Salvează modificarea

        eventService.submitAction(
                ActionType.DISABLE,
                toggleEnvironment.getToggle().getProject(),
                toggleEnvironment.getToggle(),
                toggleEnvironment.getEnvironment()
        );
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
            return ConstraintUtil.validate(toggle.getConstraints(), contextFields);
        } else {
            return Boolean.FALSE;
        }
    }

    public String getPayloadInToggleEnv(Toggle toggle, Environment environment, Long instanceId, Boolean resultEnabled) {
        // Fetch Toggle-Environment-Instance legătura
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environment.getId(), instanceId);

        // Returnează payload-ul în funcție de starea rezultatului (enabled/disabled)
        if (resultEnabled) {
            return toggleEnvironment.getEnabledValue();
        } else {
            return toggleEnvironment.getDisabledValue();
        }
    }


    public void deleteEnvironmentToggleAssociations(Environment environment) {
        toggleEnvironmentRepository.deleteAllByEnvironment(environment);
    }

    @Transactional
    public void deleteEnvironmentToggleByProjectAndEnvIdAndInstanceId(Project project, Long envId, Long instanceId) {
        // Obține mediul
        Environment environment = fetchEnvironmentById(envId);

        // Șterge toate legăturile Toggle-Environment-Instance
        toggleEnvironmentRepository.deleteAllByToggle_ProjectAndEnvironmentAndInstance(project, environment, instanceId);
    }

    @Transactional
    public void removePayload(Toggle toggle, Long environmentId, Long instanceId) {
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggle.getId(), environmentId, instanceId);
        toggleEnvironment.setEnabledValue(null);
        toggleEnvironment.setDisabledValue(null);
        toggleEnvironmentRepository.save(toggleEnvironment); // Persistăm modificările
    }


    public List<ToggleEnvironment> findAllToggleEnvironments() {
        return toggleEnvironmentRepository.findAll();
    }

    public ToggleEnvironment saveToggleEnvironment(ToggleEnvironment toggleEnvironment) {
        return toggleEnvironmentRepository.save(toggleEnvironment);
    }

    public ToggleEnvironment setToggleSchedule(Long toggleId, Long environmentId, Long instanceId,
                                               LocalTime startOn, LocalTime startOff,
                                               LocalDate startDate, LocalDate endDate) {
        // Obține relația Toggle-Environment-Instance
        ToggleEnvironment toggleEnvironment = fetchByToggleIdAndEnvIdAndInstanceId(toggleId, environmentId, instanceId);

        // Setează valorile pentru programare
        toggleEnvironment.setStartOn(startOn);
        toggleEnvironment.setStartOff(startOff);
        toggleEnvironment.setStartDate(startDate);
        toggleEnvironment.setEndDate(endDate);

        return toggleEnvironmentRepository.save(toggleEnvironment);
    }


    public List<ToggleEnvironment> findAllByToggleId(Long toggleId) {
        List<ToggleEnvironment> te=  toggleEnvironmentRepository.findAllByToggleId(toggleId);
        return te;
    }


    //eu
    public void createToggleEnvironmentAssociation(Toggle toggle, Environment environment, Instance instance) {
        // Verificăm dacă există deja o astfel de legătură
        ToggleEnvironment existingAssociation = toggleEnvironmentRepository.findByToggleAndEnvironmentAndInstance(toggle, environment, instance);

        if (existingAssociation==null) {
            // Creăm o nouă legătură
            ToggleEnvironment toggleEnvironment = new ToggleEnvironment();
            toggleEnvironment.setToggle(toggle);
            toggleEnvironment.setEnvironment(environment);
            toggleEnvironment.setInstance(instance);
            toggleEnvironment.setEnabled(false); // Implicit dezactivat
            toggleEnvironmentRepository.save(toggleEnvironment);
        }
    }

    public void createEnvironmentsToggleAssociations(List<Toggle> toggleList, Long environmentId, Long instanceId) {
        // Iterăm prin toate toggle-urile
        for (Toggle toggle : toggleList) {
            // Creează o nouă legătură Toggle-Environment-Instance
            //print toggle id
            System.out.println("toggle id: "+toggle.getId());

            ToggleEnvironment toggleEnvironment = new ToggleEnvironment();
            toggleEnvironment.setToggle(toggle);
            toggleEnvironment.setEnvironment(environmentRepository.findById(environmentId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found with id: " + environmentId)));
            toggleEnvironment.setInstance(instanceRepository.findById(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found with id: " + instanceId)));

            // Setăm valorile implicite
            toggleEnvironment.setEnabled(false);
            toggleEnvironmentRepository.save(toggleEnvironment); // Salvăm în baza de date
        }
    }

    public ToggleEnvironment fetchByToggleEnvAndInstanceId(Toggle toggle, Long envId, Long instanceId) {
        return toggleEnvironmentRepository.findByToggleIdAndEnvIdAndInstanceId(toggle.getId(), envId, instanceId)
                .orElse(null); // Returnăm null dacă nu există combinația
    }

    public void deleteEnvironmentToggleByProjectEnvAndInstanceId(Project project, Long envId, Long instanceId) {
        // Găsește toate legăturile pentru proiect, mediu și instanță
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentRepository.findByProjectAndEnvironmentAndInstance(project, envId, instanceId);

        // Șterge legăturile
        toggleEnvironmentRepository.deleteAll(toggleEnvironments);
    }

    public List<ToggleEnvironment> findAllByToggleIdAndInstanceId(Long toggleId, Long instanceId) {
        return toggleEnvironmentRepository.findAllByToggleIdAndInstanceId(toggleId, instanceId);
    }

    public void deleteByEnvironmentId(Long environmentId) {
        toggleEnvironmentRepository.deleteByEnvironmentId(environmentId);
    }

}
