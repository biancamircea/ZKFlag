package ro.mta.toggleserverapi.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.DTOs.ToggleEnvironmentDTO;
import ro.mta.toggleserverapi.converters.ToggleEnvironmentConverter;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.exceptions.ToggleNotFoundException;
import ro.mta.toggleserverapi.repositories.ToggleRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleServiceTest {

    @Mock
    private ToggleRepository toggleRepository;

    @Mock private ProjectService projectService;
    @Mock private EventService eventService;
    @Mock private InstanceService instanceService;
    @Mock private ToggleEnvironmentService toggleEnvironmentService;
    @Mock private EnvironmentService environmentService;
    @Mock private InstanceEnvironmentService instanceEnvironmentService;
    @Mock private ToggleEnvironmentConverter toggleEnvironmentConverter;


    @Spy
    @InjectMocks private ToggleService toggleService;


    @Test
    void fetchAllToggles_shouldReturnAllToggles() {
        Toggle toggle1 = new Toggle();
        toggle1.setId(1L);
        toggle1.setName("Flag 1");
        toggle1.setDescription("Test description");

        Toggle toggle2 = new Toggle();
        toggle2.setId(2L);
        toggle2.setName("Flag2");
        toggle2.setDescription("Test description");

        // arrange
        List<Toggle> mockToggles = List.of(
              toggle1,toggle2
        );
        when(toggleRepository.findAll()).thenReturn(mockToggles);

        // act
        List<Toggle> result = toggleService.fetchAllToggles();

        // assert
        assertEquals(2, result.size());
        assertEquals("Flag 1", result.get(0).getName());
    }

    @Test
    void fetchToggle_shouldReturnToggleIfExists() {
        // arrange
        Toggle toggle1 = new Toggle();
        toggle1.setId(1L);
        toggle1.setName("Test Flag1");
        toggle1.setDescription("Test description");
        when(toggleRepository.findById(1L)).thenReturn(Optional.of(toggle1));

        // act
        Toggle result = toggleService.fetchToggle(1L);

        // assert
        assertNotNull(result);
        assertEquals("Test Flag1", result.getName());
    }

    @Test
    void fetchToggle_shouldThrowExceptionIfNotFound() {
        // arrange
        when(toggleRepository.findById(99L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ToggleNotFoundException.class, () -> toggleService.fetchToggle(99L));
    }


    @Test
    void saveToggle_shouldSaveToggleAndAssociateWithEnvironments() {
        // arrange
        Long projectId = 1L;
        Project mockProject = new Project();
        mockProject.setId(projectId);

        Toggle toggle = new Toggle();
        toggle.setName("Test Toggle");

        Toggle savedToggle = new Toggle();
        savedToggle.setId(123L);
        savedToggle.setName("Test Toggle");
        savedToggle.setProject(mockProject);

        when(projectService.fetchProject(projectId)).thenReturn(mockProject);
        when(toggleRepository.save(Mockito.<Toggle>any())).thenReturn(savedToggle);
        when(instanceService.fetchInstancesByProject(projectId)).thenReturn(List.of(new Instance()));
        when(instanceService.fetchEnabledEnvironmentsInInstance(Mockito.<Instance>any())).thenReturn(List.of(new Environment()));

        // act
        Toggle result = toggleService.saveToggle(toggle, projectId);

        // assert
        assertNotNull(result);
        assertEquals(123L, result.getId());
        verify(toggleRepository, times(2)).save(Mockito.<Toggle>any());



        verify(toggleEnvironmentService).createToggleEnvironmentAssociation(Mockito.<Toggle>any(), Mockito.<Environment>any(), Mockito.<Instance>any());
    }

    @Test
    void enableToggleInEnvironment_shouldEnableToggleAndTriggerEvent() {
        // arrange
        Long projectId = 1L;
        Long toggleId = 2L;
        Long instanceId = 3L;
        String envName = "prod";

        Project project = new Project();
        project.setId(projectId);

        Toggle toggle = new Toggle();
        toggle.setId(toggleId);

        Environment environment = new Environment();
        environment.setName(envName);

        Instance instance = new Instance();
        instance.setId(instanceId);

        doReturn(toggle).when(toggleService).fetchToggleByProjectIdAndToggleId(projectId, toggleId);


        when(environmentService.fetchEnvironmentByName(envName)).thenReturn(environment);
        when(instanceService.fetchInstance(instanceId)).thenReturn(instance);
        when(projectService.fetchProject(projectId)).thenReturn(project);

        // act
        toggleService.enableToggleInEnvironment(projectId, toggleId, envName, instanceId);

        // assert
        verify(eventService).submitAction(ActionType.ENABLE, project, toggle, environment, instance);
        verify(toggleEnvironmentService).enableByToggleIdEnvNameAndInstanceId(toggleId, envName, instanceId);
    }


    @Test
    void saveToggle_shouldSaveToggleAndGenerateHashId() {
        Project mockProject = new Project();
        mockProject.setId(1L);

        Toggle toggle = new Toggle();
        toggle.setName("TestFlag");

        when(projectService.fetchProject(1L)).thenReturn(mockProject); //ret pr generat de noi
        when(toggleRepository.save(any(Toggle.class))).thenAnswer(i -> { //simulam salvarea in baza de date si setamm id-ul manual
            Toggle t = i.getArgument(0);
            t.setId(100L);
            return t;
        });

        when(instanceService.fetchInstancesByProject(1L)).thenReturn(Collections.emptyList()); // Nu avem instanta pentru test

        // act
        Toggle result = toggleService.saveToggle(toggle, 1L); // Salvam toggle-ul cu hash id

        // assert
        assertNotNull(result.getHashId()); // Verificam ca hash id-ul a fost generat
        assertEquals("TestFlag", result.getName()); // Verificam ca numele este corect
        verify(toggleRepository, times(2)).save(any()); //2 apeluri de salvare (cu si fara hash id)
    }

    @Test
    void addPayloadInToggleEnvironment_shouldSavePayloadCorrectly() {
        // arrange
        Long projectId = 1L, instanceId = 1L, toggleId = 1L, envId = 2L;

        Project project = new Project();
        project.setId(projectId);

        Instance instance = new Instance();
        instance.setId(instanceId);

        Toggle toggle = new Toggle();
        toggle.setId(toggleId);
        toggle.setProject(project);

        InstanceEnvironment ie = new InstanceEnvironment();
        Environment environment = new Environment();
        environment.setId(envId);
        ie.setEnvironment(environment);

        ToggleEnvironment toggleEnv = new ToggleEnvironment();
        toggleEnv.setToggle(toggle);
        toggleEnv.setEnvironment(environment);

        ToggleEnvironmentDTO dto = new ToggleEnvironmentDTO();

        when(projectService.fetchInstanceByProjectIdAndInstanceId(projectId, instanceId)).thenReturn(instance);
        when(instanceEnvironmentService.fetchAllByInstanceIdAndActiveTrue(instanceId)).thenReturn(List.of(ie));

        when(toggleRepository.findByIdAndProjectId(toggleId, projectId)).thenReturn(Optional.of(toggle));

        when(toggleEnvironmentService.addPayload(toggle, envId, instanceId, "img1.png", "text_off")).thenReturn(toggleEnv);
        when(toggleEnvironmentConverter.toDTO(toggleEnv)).thenReturn(dto);

        ToggleEnvironmentDTO result = toggleService.addPayloadInToggleEnvironment(
                projectId, instanceId, toggleId, envId, "img1.png", "text_off"
        );

        assertNotNull(result);
        assertEquals(dto, result);
        verify(toggleEnvironmentService).addPayload(toggle, envId, instanceId, "img1.png", "text_off");
        verify(eventService).submitAction(any(), any(), any(), any());
    }


    @Test
    void evaluateToggleInContext_shouldReturnTrue_whenToggleIsValid() {
        // arrange
        Project project = new Project();
        Instance instance = new Instance();
        Environment environment = new Environment();

        ApiToken apiToken = new ApiToken();
        apiToken.setProject(project);
        apiToken.setInstance(instance);
        apiToken.setEnvironment(environment);
        apiToken.setType(1L); // backend

        Toggle toggle = new Toggle();
        toggle.setName("flag1");
        toggle.setToggleType(1);
        toggle.setProject(project);

        List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> context = new ArrayList<>();

        when(toggleRepository.findByNameAndProjectAndToggleType("flag1", project, 1))
                .thenReturn(Optional.of(toggle));
        when(toggleEnvironmentService.evaluateToggleInContext(toggle, environment, instance.getId(), context, null))
                .thenReturn(true);

        // act
        Boolean result = toggleService.evaluateToggleInContext("flag1", apiToken, context, null);

        // assert
        assertTrue(result);
    }



}
