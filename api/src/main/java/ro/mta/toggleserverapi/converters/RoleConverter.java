package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.RoleDTO;
import ro.mta.toggleserverapi.entities.Role;

public class RoleConverter {
    public static RoleDTO toDTO(Role role){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setType(role.getRoleType());
        roleDTO.setDescription(role.getDescription());
        return roleDTO;
    }
    public static Role fromDTO(RoleDTO roleDTO){
        Role role = new Role();
        role.setRoleType(roleDTO.getType());
        role.setDescription(roleDTO.getDescription());
        return role;
    }
}
