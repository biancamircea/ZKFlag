package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ro.mta.toggleserverapi.DTOs.RoleDTO;
import ro.mta.toggleserverapi.converters.RoleConverter;
import ro.mta.toggleserverapi.entities.Role;
import ro.mta.toggleserverapi.exceptions.RoleAlreadyExistsException;
import ro.mta.toggleserverapi.exceptions.RoleNotFoundException;
import ro.mta.toggleserverapi.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> fetchAll(){
        return roleRepository.findAll();
    }
    public Role fetchRoleById(Long roleId){
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));
    }
    public RoleDTO getRoleById(Long roleId) {
        Role role = fetchRoleById(roleId);
        return RoleConverter.toDTO(role);
    }

    public void assertUnique(Role role){
        Optional<Role> searchRole = roleRepository.findByRoleType(role.getRoleType());
        if(searchRole.isPresent()){
            String location = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/roles/{id}")
                    .buildAndExpand(searchRole.get().getId())
                    .toUriString();
            throw new RoleAlreadyExistsException(role.getRoleType(), location);
        }
    }
    private Role saveRole(Role role){
        assertUnique(role);
        return roleRepository.save(role);
    }
    public RoleDTO createRole(Role role) {
        Role createdRole = saveRole(role);
        return RoleConverter.toDTO(createdRole);
    }
    public RoleDTO updateRole(Role role, Long roleId) {
        Role updatedRole = roleRepository.findById(roleId)
                .map(existingRole -> {
                    existingRole.setDescription(role.getDescription());
                    if(!existingRole.getRoleType().equals(role.getRoleType())){
                        assertUnique(role);
                        existingRole.setRoleType(role.getRoleType());
                    }
                    return roleRepository.save(existingRole);
                })
                .orElseGet(() -> saveRole(role));
        return RoleConverter.toDTO(updatedRole);
    }
    public void deleteRoleById(Long roleId) {
        roleRepository.deleteById(roleId);
    }


}
