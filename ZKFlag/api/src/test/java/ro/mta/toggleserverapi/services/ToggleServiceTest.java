//package ro.mta.toggleserverapi.services;
//
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import ro.mta.toggleserverapi.converters.ConstraintConverter;
//import ro.mta.toggleserverapi.converters.ProjectOverviewConverter;
//import ro.mta.toggleserverapi.converters.ToggleConverter;
//import ro.mta.toggleserverapi.converters.ToggleEnvironmentConverter;
//import ro.mta.toggleserverapi.entities.Toggle;
//import ro.mta.toggleserverapi.exceptions.ToggleNotFoundException;
//import ro.mta.toggleserverapi.repositories.ApiTokenRepository;
//import ro.mta.toggleserverapi.repositories.ConstraintRepository;
//import ro.mta.toggleserverapi.repositories.ConstraintValueRepository;
//import ro.mta.toggleserverapi.repositories.ContextFieldRepository;
//import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
//import ro.mta.toggleserverapi.repositories.EventRepository;
//import ro.mta.toggleserverapi.repositories.ProjectEnvironmentRepository;
//import ro.mta.toggleserverapi.repositories.ProjectRepository;
//import ro.mta.toggleserverapi.repositories.ProjectRoleRepository;
//import ro.mta.toggleserverapi.repositories.TagRepository;
//import ro.mta.toggleserverapi.repositories.ToggleEnvironmentRepository;
//import ro.mta.toggleserverapi.repositories.ToggleRepository;
//import ro.mta.toggleserverapi.repositories.ToggleTagRepository;
//import ro.mta.toggleserverapi.repositories.UserProjectRepository;
//import ro.mta.toggleserverapi.repositories.UserRepository;
//
//class ToggleServiceTest {
//    /**
//     * Method under test: {@link ToggleService#fetchAllToggles()}
//     */
//    //@Test
////    void testFetchAllToggles() {
////        ToggleRepository toggleRepository = mock(ToggleRepository.class);
////        ArrayList<Toggle> toggleList = new ArrayList<>();
////        when(toggleRepository.findAll()).thenReturn(toggleList);
////        ProjectRepository projectRepository = mock(ProjectRepository.class);
////        ApiTokenRepository apiTokenRepository = mock(ApiTokenRepository.class);
////        ProjectEnvironmentRepository projectEnvironmentRepository = mock(ProjectEnvironmentRepository.class);
////        ProjectRepository projectRepository2 = mock(ProjectRepository.class);
////        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
////        ProjectEnvironmentService projectEnvironmentService = new ProjectEnvironmentService(projectEnvironmentRepository,
////                projectRepository2, environmentRepository, new EventService(mock(EventRepository.class)));
////
////        UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
////        UserRepository userRepository = mock(UserRepository.class);
////        ProjectRepository projectRepository3 = mock(ProjectRepository.class);
////        UserProjectService userProjectService = new UserProjectService(userProjectRepository, userRepository,
////                projectRepository3, new ProjectRoleService(mock(ProjectRoleRepository.class)));
////
////        ProjectRoleService projectRoleService = new ProjectRoleService(mock(ProjectRoleRepository.class));
////        ToggleConverter toggleConverter = new ToggleConverter(new ToggleEnvironmentConverter());
////        ProjectOverviewConverter projectOverviewConverter = new ProjectOverviewConverter(
////                new ToggleConverter(new ToggleEnvironmentConverter()));
////        ProjectService projectService = new ProjectService(projectRepository, apiTokenRepository, projectEnvironmentService,
////                userProjectService, projectRoleService, toggleConverter, projectOverviewConverter,
////                new EventService(mock(EventRepository.class)));
////
////        ToggleEnvironmentRepository toggleEnvironmentRepository = mock(ToggleEnvironmentRepository.class);
////        ToggleRepository toggleRepository2 = mock(ToggleRepository.class);
////        EnvironmentRepository environmentRepository2 = mock(EnvironmentRepository.class);
////        ToggleEnvironmentService toggleEnvironmentService = new ToggleEnvironmentService(toggleEnvironmentRepository,
////                toggleRepository2, environmentRepository2, new EventService(mock(EventRepository.class)));
////
////        ToggleTagService toggleTagService = new ToggleTagService(mock(ToggleTagRepository.class),
////                mock(ToggleRepository.class), mock(TagRepository.class));
////
////        ConstraintConverter constraintConverter = new ConstraintConverter();
////        ConstraintRepository constraintRepository = mock(ConstraintRepository.class);
////        ConstraintValueRepository valueRepository = mock(ConstraintValueRepository.class);
////        ToggleEnvironmentRepository toggleEnvironmentRepository2 = mock(ToggleEnvironmentRepository.class);
////        ToggleRepository toggleRepository3 = mock(ToggleRepository.class);
////        EnvironmentRepository environmentRepository3 = mock(EnvironmentRepository.class);
////        ToggleEnvironmentService toggleEnvironmentService2 = new ToggleEnvironmentService(toggleEnvironmentRepository2,
////                toggleRepository3, environmentRepository3, new EventService(mock(EventRepository.class)));
////
////        ContextFieldRepository contextFieldRepository = mock(ContextFieldRepository.class);
////        ProjectRepository projectRepository4 = mock(ProjectRepository.class);
////        ApiTokenRepository apiTokenRepository2 = mock(ApiTokenRepository.class);
////        ProjectEnvironmentService projectEnvironmentService2 = new ProjectEnvironmentService(
////                mock(ProjectEnvironmentRepository.class), mock(ProjectRepository.class), mock(EnvironmentRepository.class),
////                null);
////
////        UserProjectService userProjectService2 = new UserProjectService(mock(UserProjectRepository.class),
////                mock(UserRepository.class), mock(ProjectRepository.class), null);
////
////        ProjectRoleService projectRoleService2 = new ProjectRoleService(mock(ProjectRoleRepository.class));
////        ToggleConverter toggleConverter2 = new ToggleConverter(new ToggleEnvironmentConverter());
////        ProjectOverviewConverter projectOverviewConverter2 = new ProjectOverviewConverter(null);
////        ConstraintService constraintService = new ConstraintService(constraintRepository, valueRepository,
////                toggleEnvironmentService2,
////                new ContextFieldService(contextFieldRepository,
////                        new ProjectService(projectRepository4, apiTokenRepository2, projectEnvironmentService2, userProjectService2,
////                                projectRoleService2, toggleConverter2, projectOverviewConverter2,
////                                new EventService(mock(EventRepository.class)))));
////
////        ToggleEnvironmentConverter toggleEnvironmentConverter = new ToggleEnvironmentConverter();
////        List<Toggle> actualFetchAllTogglesResult = (new ToggleService(toggleRepository, projectService,
////                toggleEnvironmentService, toggleTagService, constraintConverter, constraintService, toggleEnvironmentConverter,
////                new EventService(mock(EventRepository.class)))).fetchAllToggles();
////        assertSame(toggleList, actualFetchAllTogglesResult);
////        assertTrue(actualFetchAllTogglesResult.isEmpty());
////        verify(toggleRepository).findAll();
////    }
//
//    /**
//     * Method under test: {@link ToggleService#fetchAllToggles()}
//     */
////    @Test
////    void testFetchAllToggles2() {
////        ToggleRepository toggleRepository = mock(ToggleRepository.class);
////        when(toggleRepository.findAll()).thenThrow(new ToggleNotFoundException(1L));
////        ProjectRepository projectRepository = mock(ProjectRepository.class);
////        ApiTokenRepository apiTokenRepository = mock(ApiTokenRepository.class);
////        ProjectEnvironmentRepository projectEnvironmentRepository = mock(ProjectEnvironmentRepository.class);
////        ProjectRepository projectRepository2 = mock(ProjectRepository.class);
////        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
////        ProjectEnvironmentService projectEnvironmentService = new ProjectEnvironmentService(projectEnvironmentRepository,
////                projectRepository2, environmentRepository, new EventService(mock(EventRepository.class)));
////
////        UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
////        UserRepository userRepository = mock(UserRepository.class);
////        ProjectRepository projectRepository3 = mock(ProjectRepository.class);
////        UserProjectService userProjectService = new UserProjectService(userProjectRepository, userRepository,
////                projectRepository3, new ProjectRoleService(mock(ProjectRoleRepository.class)));
////
////        ProjectRoleService projectRoleService = new ProjectRoleService(mock(ProjectRoleRepository.class));
////        ToggleConverter toggleConverter = new ToggleConverter(new ToggleEnvironmentConverter());
////        ProjectOverviewConverter projectOverviewConverter = new ProjectOverviewConverter(
////                new ToggleConverter(new ToggleEnvironmentConverter()));
////        ProjectService projectService = new ProjectService(projectRepository, apiTokenRepository,
////                projectEnvironmentService, userProjectService, projectRoleService, toggleConverter, projectOverviewConverter,
////                new EventService(mock(EventRepository.class)));
////
////        ToggleEnvironmentRepository toggleEnvironmentRepository = mock(ToggleEnvironmentRepository.class);
////        ToggleRepository toggleRepository2 = mock(ToggleRepository.class);
////        EnvironmentRepository environmentRepository2 = mock(EnvironmentRepository.class);
////        ToggleEnvironmentService toggleEnvironmentService = new ToggleEnvironmentService(toggleEnvironmentRepository,
////                toggleRepository2, environmentRepository2, new EventService(mock(EventRepository.class)));
////
////        ToggleTagService toggleTagService = new ToggleTagService(mock(ToggleTagRepository.class),
////                mock(ToggleRepository.class), mock(TagRepository.class));
////
////        ConstraintConverter constraintConverter = new ConstraintConverter();
////        ConstraintRepository constraintRepository = mock(ConstraintRepository.class);
////        ConstraintValueRepository valueRepository = mock(ConstraintValueRepository.class);
////        ToggleEnvironmentRepository toggleEnvironmentRepository2 = mock(ToggleEnvironmentRepository.class);
////        ToggleRepository toggleRepository3 = mock(ToggleRepository.class);
////        EnvironmentRepository environmentRepository3 = mock(EnvironmentRepository.class);
////        ToggleEnvironmentService toggleEnvironmentService2 = new ToggleEnvironmentService(toggleEnvironmentRepository2,
////                toggleRepository3, environmentRepository3, new EventService(mock(EventRepository.class)));
////
////        ContextFieldRepository contextFieldRepository = mock(ContextFieldRepository.class);
////        ProjectRepository projectRepository4 = mock(ProjectRepository.class);
////        ApiTokenRepository apiTokenRepository2 = mock(ApiTokenRepository.class);
////        ProjectEnvironmentService projectEnvironmentService2 = new ProjectEnvironmentService(
////                mock(ProjectEnvironmentRepository.class), mock(ProjectRepository.class), mock(EnvironmentRepository.class),
////                null);
////
////        UserProjectService userProjectService2 = new UserProjectService(mock(UserProjectRepository.class),
////                mock(UserRepository.class), mock(ProjectRepository.class), null);
////
////        ProjectRoleService projectRoleService2 = new ProjectRoleService(mock(ProjectRoleRepository.class));
////        ToggleConverter toggleConverter2 = new ToggleConverter(new ToggleEnvironmentConverter());
////        ProjectOverviewConverter projectOverviewConverter2 = new ProjectOverviewConverter(null);
////        ConstraintService constraintService = new ConstraintService(constraintRepository, valueRepository,
////                toggleEnvironmentService2,
////                new ContextFieldService(contextFieldRepository,
////                        new ProjectService(projectRepository4, apiTokenRepository2, projectEnvironmentService2,
////                                userProjectService2, projectRoleService2, toggleConverter2, projectOverviewConverter2,
////                                new EventService(mock(EventRepository.class)))));
////
////        ToggleEnvironmentConverter toggleEnvironmentConverter = new ToggleEnvironmentConverter();
////        assertThrows(ToggleNotFoundException.class,
////                () -> (new ToggleService(toggleRepository, projectService, toggleEnvironmentService, toggleTagService,
////                        constraintConverter, constraintService, toggleEnvironmentConverter,
////                        new EventService(mock(EventRepository.class)))).fetchAllToggles());
////        verify(toggleRepository).findAll();
////    }
//
//    /**
//     * Method under test: {@link ToggleService#fetchToggleByProjectIdAndToggleId(Long, Long)}
//     */
//    @Test
//    void testFetchToggleByProjectIdAndToggleId() {
//        ToggleRepository toggleRepository = mock(ToggleRepository.class);
//        Toggle toggle = new Toggle();
//        when(toggleRepository.findByIdAndProjectId(Mockito.<Long>any(), Mockito.<Long>any()))
//                .thenReturn(Optional.of(toggle));
//        ProjectRepository projectRepository = mock(ProjectRepository.class);
//        ApiTokenRepository apiTokenRepository = mock(ApiTokenRepository.class);
//        ProjectEnvironmentRepository projectEnvironmentRepository = mock(ProjectEnvironmentRepository.class);
//        ProjectRepository projectRepository2 = mock(ProjectRepository.class);
//        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
//        ProjectEnvironmentService projectEnvironmentService = new ProjectEnvironmentService(projectEnvironmentRepository,
//                projectRepository2, environmentRepository, new EventService(mock(EventRepository.class)));
//
//        UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
//        UserRepository userRepository = mock(UserRepository.class);
//        ProjectRepository projectRepository3 = mock(ProjectRepository.class);
//        UserProjectService userProjectService = new UserProjectService(userProjectRepository, userRepository,
//                projectRepository3, new ProjectRoleService(mock(ProjectRoleRepository.class)));
//
//        ProjectRoleService projectRoleService = new ProjectRoleService(mock(ProjectRoleRepository.class));
//        ToggleConverter toggleConverter = new ToggleConverter(new ToggleEnvironmentConverter());
//        ProjectOverviewConverter projectOverviewConverter = new ProjectOverviewConverter(
//                new ToggleConverter(new ToggleEnvironmentConverter()));
//        ProjectService projectService = new ProjectService(projectRepository, apiTokenRepository, projectEnvironmentService,
//                userProjectService, projectRoleService, toggleConverter, projectOverviewConverter,
//                new EventService(mock(EventRepository.class)));
//
//        ToggleEnvironmentRepository toggleEnvironmentRepository = mock(ToggleEnvironmentRepository.class);
//        ToggleRepository toggleRepository2 = mock(ToggleRepository.class);
//        EnvironmentRepository environmentRepository2 = mock(EnvironmentRepository.class);
//        ToggleEnvironmentService toggleEnvironmentService = new ToggleEnvironmentService(toggleEnvironmentRepository,
//                toggleRepository2, environmentRepository2, new EventService(mock(EventRepository.class)));
//
//        ToggleTagService toggleTagService = new ToggleTagService(mock(ToggleTagRepository.class),
//                mock(ToggleRepository.class), mock(TagRepository.class));
//
//        ConstraintConverter constraintConverter = new ConstraintConverter();
//        ConstraintRepository constraintRepository = mock(ConstraintRepository.class);
//        ConstraintValueRepository valueRepository = mock(ConstraintValueRepository.class);
//        ToggleEnvironmentRepository toggleEnvironmentRepository2 = mock(ToggleEnvironmentRepository.class);
//        ToggleRepository toggleRepository3 = mock(ToggleRepository.class);
//        EnvironmentRepository environmentRepository3 = mock(EnvironmentRepository.class);
//        ToggleEnvironmentService toggleEnvironmentService2 = new ToggleEnvironmentService(toggleEnvironmentRepository2,
//                toggleRepository3, environmentRepository3, new EventService(mock(EventRepository.class)));
//
//        ContextFieldRepository contextFieldRepository = mock(ContextFieldRepository.class);
//        ProjectRepository projectRepository4 = mock(ProjectRepository.class);
//        ApiTokenRepository apiTokenRepository2 = mock(ApiTokenRepository.class);
//        ProjectEnvironmentService projectEnvironmentService2 = new ProjectEnvironmentService(
//                mock(ProjectEnvironmentRepository.class), mock(ProjectRepository.class), mock(EnvironmentRepository.class),
//                null);
//
//        UserProjectService userProjectService2 = new UserProjectService(mock(UserProjectRepository.class),
//                mock(UserRepository.class), mock(ProjectRepository.class), null);
//
//        ProjectRoleService projectRoleService2 = new ProjectRoleService(mock(ProjectRoleRepository.class));
//        ToggleConverter toggleConverter2 = new ToggleConverter(new ToggleEnvironmentConverter());
//        ProjectOverviewConverter projectOverviewConverter2 = new ProjectOverviewConverter(null);
//        ConstraintService constraintService = new ConstraintService(constraintRepository, valueRepository,
//                toggleEnvironmentService2,
//                new ContextFieldService(contextFieldRepository,
//                        new ProjectService(projectRepository4, apiTokenRepository2, projectEnvironmentService2, userProjectService2,
//                                projectRoleService2, toggleConverter2, projectOverviewConverter2,
//                                new EventService(mock(EventRepository.class)))));
//
//        ToggleEnvironmentConverter toggleEnvironmentConverter = new ToggleEnvironmentConverter();
//        assertSame(toggle,
//                (new ToggleService(toggleRepository, projectService, toggleEnvironmentService, toggleTagService,
//                        constraintConverter, constraintService, toggleEnvironmentConverter,
//                        new EventService(mock(EventRepository.class)))).fetchToggleByProjectIdAndToggleId(1L, 1L));
//        verify(toggleRepository).findByIdAndProjectId(Mockito.<Long>any(), Mockito.<Long>any());
//    }
//
//    /**
//     * Method under test: {@link ToggleService#fetchToggleByProjectIdAndToggleId(Long, Long)}
//     */
//    @Test
//    void testFetchToggleByProjectIdAndToggleId2() {
//        ToggleRepository toggleRepository = mock(ToggleRepository.class);
//        when(toggleRepository.findByIdAndProjectId(Mockito.<Long>any(), Mockito.<Long>any()))
//                .thenReturn(Optional.empty());
//        ProjectRepository projectRepository = mock(ProjectRepository.class);
//        ApiTokenRepository apiTokenRepository = mock(ApiTokenRepository.class);
//        ProjectEnvironmentRepository projectEnvironmentRepository = mock(ProjectEnvironmentRepository.class);
//        ProjectRepository projectRepository2 = mock(ProjectRepository.class);
//        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
//        ProjectEnvironmentService projectEnvironmentService = new ProjectEnvironmentService(projectEnvironmentRepository,
//                projectRepository2, environmentRepository, new EventService(mock(EventRepository.class)));
//
//        UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
//        UserRepository userRepository = mock(UserRepository.class);
//        ProjectRepository projectRepository3 = mock(ProjectRepository.class);
//        UserProjectService userProjectService = new UserProjectService(userProjectRepository, userRepository,
//                projectRepository3, new ProjectRoleService(mock(ProjectRoleRepository.class)));
//
//        ProjectRoleService projectRoleService = new ProjectRoleService(mock(ProjectRoleRepository.class));
//        ToggleConverter toggleConverter = new ToggleConverter(new ToggleEnvironmentConverter());
//        ProjectOverviewConverter projectOverviewConverter = new ProjectOverviewConverter(
//                new ToggleConverter(new ToggleEnvironmentConverter()));
//        ProjectService projectService = new ProjectService(projectRepository, apiTokenRepository,
//                projectEnvironmentService, userProjectService, projectRoleService, toggleConverter, projectOverviewConverter,
//                new EventService(mock(EventRepository.class)));
//
//        ToggleEnvironmentRepository toggleEnvironmentRepository = mock(ToggleEnvironmentRepository.class);
//        ToggleRepository toggleRepository2 = mock(ToggleRepository.class);
//        EnvironmentRepository environmentRepository2 = mock(EnvironmentRepository.class);
//        ToggleEnvironmentService toggleEnvironmentService = new ToggleEnvironmentService(toggleEnvironmentRepository,
//                toggleRepository2, environmentRepository2, new EventService(mock(EventRepository.class)));
//
//        ToggleTagService toggleTagService = new ToggleTagService(mock(ToggleTagRepository.class),
//                mock(ToggleRepository.class), mock(TagRepository.class));
//
//        ConstraintConverter constraintConverter = new ConstraintConverter();
//        ConstraintRepository constraintRepository = mock(ConstraintRepository.class);
//        ConstraintValueRepository valueRepository = mock(ConstraintValueRepository.class);
//        ToggleEnvironmentRepository toggleEnvironmentRepository2 = mock(ToggleEnvironmentRepository.class);
//        ToggleRepository toggleRepository3 = mock(ToggleRepository.class);
//        EnvironmentRepository environmentRepository3 = mock(EnvironmentRepository.class);
//        ToggleEnvironmentService toggleEnvironmentService2 = new ToggleEnvironmentService(toggleEnvironmentRepository2,
//                toggleRepository3, environmentRepository3, new EventService(mock(EventRepository.class)));
//
//        ContextFieldRepository contextFieldRepository = mock(ContextFieldRepository.class);
//        ProjectRepository projectRepository4 = mock(ProjectRepository.class);
//        ApiTokenRepository apiTokenRepository2 = mock(ApiTokenRepository.class);
//        ProjectEnvironmentService projectEnvironmentService2 = new ProjectEnvironmentService(
//                mock(ProjectEnvironmentRepository.class), mock(ProjectRepository.class), mock(EnvironmentRepository.class),
//                null);
//
//        UserProjectService userProjectService2 = new UserProjectService(mock(UserProjectRepository.class),
//                mock(UserRepository.class), mock(ProjectRepository.class), null);
//
//        ProjectRoleService projectRoleService2 = new ProjectRoleService(mock(ProjectRoleRepository.class));
//        ToggleConverter toggleConverter2 = new ToggleConverter(new ToggleEnvironmentConverter());
//        ProjectOverviewConverter projectOverviewConverter2 = new ProjectOverviewConverter(null);
//        ConstraintService constraintService = new ConstraintService(constraintRepository, valueRepository,
//                toggleEnvironmentService2,
//                new ContextFieldService(contextFieldRepository,
//                        new ProjectService(projectRepository4, apiTokenRepository2, projectEnvironmentService2,
//                                userProjectService2, projectRoleService2, toggleConverter2, projectOverviewConverter2,
//                                new EventService(mock(EventRepository.class)))));
//
//        ToggleEnvironmentConverter toggleEnvironmentConverter = new ToggleEnvironmentConverter();
//        assertThrows(ToggleNotFoundException.class,
//                () -> (new ToggleService(toggleRepository, projectService, toggleEnvironmentService, toggleTagService,
//                        constraintConverter, constraintService, toggleEnvironmentConverter,
//                        new EventService(mock(EventRepository.class)))).fetchToggleByProjectIdAndToggleId(1L, 1L));
//        verify(toggleRepository).findByIdAndProjectId(Mockito.<Long>any(), Mockito.<Long>any());
//    }
//
//    /**
//     * Method under test: {@link ToggleService#fetchToggleByProjectIdAndToggleId(Long, Long)}
//     */
//    @Test
//    void testFetchToggleByProjectIdAndToggleId3() {
//        ToggleRepository toggleRepository = mock(ToggleRepository.class);
//        when(toggleRepository.findByIdAndProjectId(Mockito.<Long>any(), Mockito.<Long>any()))
//                .thenThrow(new ToggleNotFoundException(1L));
//        ProjectRepository projectRepository = mock(ProjectRepository.class);
//        ApiTokenRepository apiTokenRepository = mock(ApiTokenRepository.class);
//        ProjectEnvironmentRepository projectEnvironmentRepository = mock(ProjectEnvironmentRepository.class);
//        ProjectRepository projectRepository2 = mock(ProjectRepository.class);
//        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
//        ProjectEnvironmentService projectEnvironmentService = new ProjectEnvironmentService(projectEnvironmentRepository,
//                projectRepository2, environmentRepository, new EventService(mock(EventRepository.class)));
//
//        UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
//        UserRepository userRepository = mock(UserRepository.class);
//        ProjectRepository projectRepository3 = mock(ProjectRepository.class);
//        UserProjectService userProjectService = new UserProjectService(userProjectRepository, userRepository,
//                projectRepository3, new ProjectRoleService(mock(ProjectRoleRepository.class)));
//
//        ProjectRoleService projectRoleService = new ProjectRoleService(mock(ProjectRoleRepository.class));
//        ToggleConverter toggleConverter = new ToggleConverter(new ToggleEnvironmentConverter());
//        ProjectOverviewConverter projectOverviewConverter = new ProjectOverviewConverter(
//                new ToggleConverter(new ToggleEnvironmentConverter()));
//        ProjectService projectService = new ProjectService(projectRepository, apiTokenRepository,
//                projectEnvironmentService, userProjectService, projectRoleService, toggleConverter, projectOverviewConverter,
//                new EventService(mock(EventRepository.class)));
//
//        ToggleEnvironmentRepository toggleEnvironmentRepository = mock(ToggleEnvironmentRepository.class);
//        ToggleRepository toggleRepository2 = mock(ToggleRepository.class);
//        EnvironmentRepository environmentRepository2 = mock(EnvironmentRepository.class);
//        ToggleEnvironmentService toggleEnvironmentService = new ToggleEnvironmentService(toggleEnvironmentRepository,
//                toggleRepository2, environmentRepository2, new EventService(mock(EventRepository.class)));
//
//        ToggleTagService toggleTagService = new ToggleTagService(mock(ToggleTagRepository.class),
//                mock(ToggleRepository.class), mock(TagRepository.class));
//
//        ConstraintConverter constraintConverter = new ConstraintConverter();
//        ConstraintRepository constraintRepository = mock(ConstraintRepository.class);
//        ConstraintValueRepository valueRepository = mock(ConstraintValueRepository.class);
//        ToggleEnvironmentRepository toggleEnvironmentRepository2 = mock(ToggleEnvironmentRepository.class);
//        ToggleRepository toggleRepository3 = mock(ToggleRepository.class);
//        EnvironmentRepository environmentRepository3 = mock(EnvironmentRepository.class);
//        ToggleEnvironmentService toggleEnvironmentService2 = new ToggleEnvironmentService(toggleEnvironmentRepository2,
//                toggleRepository3, environmentRepository3, new EventService(mock(EventRepository.class)));
//
//        ContextFieldRepository contextFieldRepository = mock(ContextFieldRepository.class);
//        ProjectRepository projectRepository4 = mock(ProjectRepository.class);
//        ApiTokenRepository apiTokenRepository2 = mock(ApiTokenRepository.class);
//        ProjectEnvironmentService projectEnvironmentService2 = new ProjectEnvironmentService(
//                mock(ProjectEnvironmentRepository.class), mock(ProjectRepository.class), mock(EnvironmentRepository.class),
//                null);
//
//        UserProjectService userProjectService2 = new UserProjectService(mock(UserProjectRepository.class),
//                mock(UserRepository.class), mock(ProjectRepository.class), null);
//
//        ProjectRoleService projectRoleService2 = new ProjectRoleService(mock(ProjectRoleRepository.class));
//        ToggleConverter toggleConverter2 = new ToggleConverter(new ToggleEnvironmentConverter());
//        ProjectOverviewConverter projectOverviewConverter2 = new ProjectOverviewConverter(null);
//        ConstraintService constraintService = new ConstraintService(constraintRepository, valueRepository,
//                toggleEnvironmentService2,
//                new ContextFieldService(contextFieldRepository,
//                        new ProjectService(projectRepository4, apiTokenRepository2, projectEnvironmentService2,
//                                userProjectService2, projectRoleService2, toggleConverter2, projectOverviewConverter2,
//                                new EventService(mock(EventRepository.class)))));
//
//        ToggleEnvironmentConverter toggleEnvironmentConverter = new ToggleEnvironmentConverter();
//        assertThrows(ToggleNotFoundException.class,
//                () -> (new ToggleService(toggleRepository, projectService, toggleEnvironmentService, toggleTagService,
//                        constraintConverter, constraintService, toggleEnvironmentConverter,
//                        new EventService(mock(EventRepository.class)))).fetchToggleByProjectIdAndToggleId(1L, 1L));
//        verify(toggleRepository).findByIdAndProjectId(Mockito.<Long>any(), Mockito.<Long>any());
//    }
//}
//
