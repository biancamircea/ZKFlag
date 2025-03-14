//package ro.mta.toggleserverapi.converters;
//
//import ro.mta.toggleserverapi.DTOs.ProjectRoleDTO;
//import ro.mta.toggleserverapi.DTOs.ProjectRolesResponseDTO;
//import ro.mta.toggleserverapi.entities.ProjectRole;
//
//import java.util.List;
//
//public class ProjectRoleConverter {
//    public static ProjectRoleDTO toDTO(ProjectRole projectRole){
//        ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
//        projectRoleDTO.setId(projectRole.getId());
//        projectRoleDTO.setType(projectRole.getRoleType());
//        projectRoleDTO.setDescription(projectRole.getDescription());
//        return projectRoleDTO;
//    }
//    public static ProjectRole fromDTO(ProjectRoleDTO projectRoleDTO){
//        ProjectRole projectRole = new ProjectRole();
//        projectRole.setRoleType(projectRoleDTO.getType());
//        projectRole.setDescription(projectRoleDTO.getDescription());
//        return projectRole;
//    }
//
//    public static ProjectRolesResponseDTO toDTOList(List<ProjectRole> projectRoles){
//        ProjectRolesResponseDTO projectRolesResponseDTO = new ProjectRolesResponseDTO();
//        List<ProjectRoleDTO> projectRoleDTOList = projectRoles
//                .stream()
//                .map(ProjectRoleConverter::toDTO)
//                .toList();
//        projectRolesResponseDTO.setProjectRoleDTOList(projectRoleDTOList);
//        return projectRolesResponseDTO;
//    }
//}
