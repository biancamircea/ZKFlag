package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.*;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.exceptions.ProjectNotFoundException;
import ro.mta.toggleserverapi.repositories.ContextFieldRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.repositories.TagRepository;
import ro.mta.toggleserverapi.repositories.ToggleRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserProjectService userProjectService;
    private final ToggleConverter toggleConverter;
    private final ProjectOverviewConverter projectOverviewConverter;
    private final InstanceService instanceService;
    private final UserConverter userConverter;


    public List<Project> fetchAllProjects() {
        return projectRepository.findAll();
    }

    public ProjectsResponseDTO getAllProjects(){
        List<Project> projectList = fetchAllProjects();
        List<ProjectDTO> projectDTOList = projectList.stream()
                .map(ProjectConverter::toDTO)
                .collect(Collectors.toList());
        ProjectsResponseDTO projectsResponseDTO = new ProjectsResponseDTO();
        projectsResponseDTO.setProjectDTOList(projectDTOList);
        return projectsResponseDTO;
    }

    @Transactional
    public Project saveProject(Project project) {
        // Salvăm proiectul pentru a obține ID-ul generat
        Project savedProject = projectRepository.save(project);

        // După ce ID-ul este generat, generăm hashId
        if (savedProject.getHashId() == null || savedProject.getHashId().isEmpty()) {
            Hashids hashids = new Hashids("project-salt", 8);
            savedProject.setHashId(hashids.encode(savedProject.getId()));

            // Salvăm din nou proiectul cu hashId generat
            savedProject = projectRepository.save(savedProject);
        }

        return savedProject;
    }

    public Project fetchProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public List<Toggle> fetchTogglesFromProject(Project project) {
        return fetchProject(project.getId()).getToggleList();
    }


    private void addTogglesToProjectOverviewDTO(ProjectOverviewDTO projectOverviewDTO, Project project){
//        get list of toggles
        List<Toggle> toggleList = fetchTogglesFromProject(project);

//        convert in DTOs objects
        List<ToggleDTO> toggleDTOList = toggleList.stream()
                .map(toggleConverter::toDTO)
                .collect(Collectors.toList());

//        set list of toggles
        projectOverviewDTO.setToggles(toggleDTOList);
    }

    public ProjectOverviewDTO makeProjectOverviewFromProject(Project project){
//        create DTO with static fields
        ProjectOverviewDTO projectOverviewDTO = ProjectOverviewDTO.toDTO(project);

        addTogglesToProjectOverviewDTO(projectOverviewDTO, project);

        return projectOverviewDTO;
    }

    public ProjectOverviewDTO getProjectOverview(Long id){
//        fetch project
        Project project = fetchProject(id);
//        return makeProjectOverviewFromProject(project);
        return projectOverviewConverter.toDTO(project);
    }

    public ProjectAccessDTO getProjectAccess(Long projectId) {
//        get project
        Project project = fetchProject(projectId);

        List<UserProject> userProjects = project.getUserProjectRole();

        return ProjectAccessConverter.toDTO(userProjects);
    }

    public Toggle saveProjectToggle(Toggle toggle, Long id) {
        return projectRepository.findById(id)
                .map( foundProject -> {
                    toggle.setProject(foundProject);
                    foundProject.getToggleList().add(toggle);
                    projectRepository.save(foundProject);
                    return foundProject.getToggleList().get(foundProject.getToggleList().size() - 1);
                })
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }


    public void addAccessToProject(List<User> users, Long projectId) {

        Project project = fetchProject(projectId);
        for(User user : users){
            userProjectService.addAccessToProject(user, project);
        }
    }

    @Transactional
    public Project updateProject(Project project, Long id) {
        return projectRepository.findById(id)
                .map( foundProject -> {
                    foundProject.setName(project.getName());
                    foundProject.setDescription(project.getDescription());
                    return projectRepository.save(foundProject);
                })
                .orElseGet(() -> saveProject(project));
    }



    @Transactional
    public void deleteProject(Long id) {
        Project project = fetchProject(id);
        projectRepository.deleteById(id);
    }

    public Project fetchProjectByInstanceId(Long instanceId) {
        Instance instance = instanceService.fetchInstance(instanceId);
        return instance.getProject();
    }


    public Instance fetchInstanceByProjectIdAndInstanceId(Long projectId, Long instanceId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found with id: " + projectId));

        return project.getInstanceList().stream()
                .filter(instance -> instance.getId().equals(instanceId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Instance not found with id: " + instanceId + " in project with id: " + projectId));
    }

    public List<UserDTO> getUsersWithProjectAdminRole(Long projectId) {
        Project project = fetchProject(projectId);
        List<UserProject> userProjects = project.getUserProjectRole();
        return userProjects.stream()
                .filter(userProject -> userProject.getUser().getRole().getRoleType().name().equals("ProjectAdmin"))
                .map(UserProject::getUser)
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

}
