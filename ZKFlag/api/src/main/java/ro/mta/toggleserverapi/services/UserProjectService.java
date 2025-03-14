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

    public UserProject fetchByProjectAndUserId(Project project, Long userId){
        return userProjectRepository.findByProjectAndUserId(project, userId)
                .orElseThrow(() -> new UserNotFoundException(userId, project.getId()));
    }
    public void saveUserProject(User user, Project project){
        UserProjectKey userProjectKey = new UserProjectKey();
        userProjectKey.setUserId(user.getId());
        userProjectKey.setProjectId(project.getId());

        UserProject userProject = new UserProject();
        userProject.setId(userProjectKey);
        userProject.setUser(user);
        userProject.setProject(project);
        userProject.setAddedAt(LocalDateTime.now());

        userProjectRepository.save(userProject);
    }
    public void addAccessToProject(User user, Project project) {
        saveUserProject(user, project);
    }

    @Transactional
    public void removeAccessFromProject(Project project, Long userId){
        userProjectRepository.deleteByProjectAndUserId(project, userId);
    }

    public List<UserProject> getUserProjectByProjectId(Long projectId) {
        return userProjectRepository.findAllByProjectId(projectId);
    }

    public List<UserProject> getUserProjectsByUserId(Long userId) {
        return userProjectRepository.findByUserId(userId);
    }
}
