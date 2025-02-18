package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.exceptions.UserNotFoundException;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.repositories.UserProjectRepository;
import ro.mta.toggleserverapi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class UserProjectService {
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRoleService projectRoleService;

    public UserProject fetchByProjectAndUserId(Project project, Long userId){
        return userProjectRepository.findByProjectAndUserId(project, userId)
                .orElseThrow(() -> new UserNotFoundException(userId, project.getId()));
    }
    public void saveUserProjectWithRole(User user, Project project, ProjectRole projectRole){
        UserProjectKey userProjectKey = new UserProjectKey();
        userProjectKey.setUserId(user.getId());
        userProjectKey.setProjectId(project.getId());

        UserProject userProject = new UserProject();
        userProject.setId(userProjectKey);
        userProject.setUser(user);
        userProject.setProject(project);
        userProject.setProjectRole(projectRole);
        userProject.setAddedAt(LocalDateTime.now());

        userProjectRepository.save(userProject);
    }
    public void addAccessToProject(User user, Project project, Long roleId) {
        ProjectRole projectRole = projectRoleService.fetchById(roleId);
        saveUserProjectWithRole(user, project, projectRole);
    }

    @Transactional
    public void updateRoleForUser(Project project, Long userId, Long roleId) {
//        get User-Project link
        UserProject userProject = fetchByProjectAndUserId(project, userId);
//        get role
        ProjectRole projectRole = projectRoleService.fetchById(roleId);
//        change project Role
        userProject.setProjectRole(projectRole);
    }

    @Transactional
    public void removeAccessFromProject(Project project, Long userId, Long roleId){
        ProjectRole projectRole = projectRoleService.fetchById(roleId);
        userProjectRepository.deleteByProjectAndUserIdAndProjectRole(project, userId, projectRole);
    }


    public List<UserProject> getUserProjectByProjectId(Long projectId) {
        return userProjectRepository.findAllByProjectId(projectId);
    }
}
