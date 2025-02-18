package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ro.mta.toggleserverapi.DTOs.ProjectRoleDTO;
import ro.mta.toggleserverapi.DTOs.ProjectRolesResponseDTO;
import ro.mta.toggleserverapi.converters.ProjectRoleConverter;
import ro.mta.toggleserverapi.entities.ProjectRole;
import ro.mta.toggleserverapi.exceptions.ProjectRoleAlreadyExistsException;
import ro.mta.toggleserverapi.exceptions.ProjectRoleNotFoundException;
import ro.mta.toggleserverapi.repositories.ProjectRoleRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProjectRoleService {
    private final ProjectRoleRepository projectRoleRepository;

    public ProjectRole fetchById(Long projectRoleId){
        return projectRoleRepository.findById(projectRoleId)
                .orElseThrow(() -> new ProjectRoleNotFoundException(projectRoleId));
    }
    public ProjectRoleDTO getProjectRoleById(Long projectRoleId) {
        ProjectRole projectRole = fetchById(projectRoleId);
        return ProjectRoleConverter.toDTO(projectRole);
    }

    public List<ProjectRole> fetchAllProjectRoles(){
        return projectRoleRepository.findAll();
    }

    public ProjectRolesResponseDTO getAllProjectRoles() {
        List<ProjectRole> projectRoleList = fetchAllProjectRoles();
        return ProjectRoleConverter.toDTOList(projectRoleList);
    }


    public void assertUnique(ProjectRole projectRole){
        Optional<ProjectRole> searchProjectRole = projectRoleRepository.findByRoleType(projectRole.getRoleType());
        if(searchProjectRole.isPresent()){
            String location = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/project-roles/{id}")
                    .buildAndExpand(searchProjectRole.get().getId())
                    .toUriString();
            throw new ProjectRoleAlreadyExistsException(projectRole.getRoleType(), location);
        }
    }

    public ProjectRole saveProjectRole(ProjectRole projectRole){
        assertUnique(projectRole);
        return projectRoleRepository.save(projectRole);
    }

    public ProjectRoleDTO createProjectRole(ProjectRole projectRole) {
        ProjectRole createdProjectRole = saveProjectRole(projectRole);
        return ProjectRoleConverter.toDTO(createdProjectRole);
    }

    public ProjectRoleDTO updateProjectRole(ProjectRole projectRole, Long projectRoleId) {
        ProjectRole updatedProjectRole = projectRoleRepository.findById(projectRoleId)
                .map(existingProjectRole -> {
                    existingProjectRole.setDescription(projectRole.getDescription());
                    if(!existingProjectRole.getRoleType().equals(projectRole.getRoleType())){
                        assertUnique(projectRole);
                        existingProjectRole.setRoleType(projectRole.getRoleType());
                    }
                    return projectRoleRepository.save(existingProjectRole);
                })
                .orElseGet(() -> saveProjectRole(projectRole));
        return ProjectRoleConverter.toDTO(updatedProjectRole);
    }


    public void deleteProjectRole(Long projectRoleId) {
        projectRoleRepository.deleteById(projectRoleId);
    }


}
