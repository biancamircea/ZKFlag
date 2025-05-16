package ro.mta.toggleserverapi.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mta.toggleserverapi.DTOs.ProjectDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.UserRoleType;
import ro.mta.toggleserverapi.repositories.UserProjectRepository;
import ro.mta.toggleserverapi.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock private UserProjectRepository userProjectRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getProjectsForProjectAdmin_shouldReturnListOfProjectDTOs() {
        // arrange
        Long userId = 1L;

        Role role = new Role();
        role.setRoleType(UserRoleType.ProjectAdmin);

        User user = new User();
        user.setId(userId);
        user.setRole(role);

        Project project = new Project();
        project.setId(1L);
        project.setHashId("abc123");
        project.setName("Test Project");
        project.setDescription("Demo project");

        // membri fictivi
        project.setUserProjectRole(List.of(new UserProject(), new UserProject()));
        // toggle-uri fictive
        project.setToggleList(List.of(new Toggle(), new Toggle(), new Toggle()));

        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProjectRepository.findByUserId(userId)).thenReturn(List.of(userProject));

        // act
        List<ProjectDTO> result = userService.getProjectsForProjectAdmin(userId);

        // assert
        assertEquals(1, result.size());
        ProjectDTO dto = result.get(0);
        assertEquals("abc123", dto.getId());
        assertEquals("Test Project", dto.getName());
        assertEquals("Demo project", dto.getDescription());
        assertEquals(2, dto.getMemberCount());
        assertEquals(3, dto.getToggleCount());
    }
}
