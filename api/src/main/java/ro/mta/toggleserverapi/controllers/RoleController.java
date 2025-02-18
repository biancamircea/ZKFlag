package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.RoleDTO;
import ro.mta.toggleserverapi.converters.RoleConverter;
import ro.mta.toggleserverapi.entities.Role;
import ro.mta.toggleserverapi.services.RoleService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping(path = "/{roleId}")
    public ResponseEntity<?> getRole(@PathVariable Long roleId){
        RoleDTO roleDTO = roleService.getRoleById(roleId);
        return ResponseEntity.ok(roleDTO);
    }
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody @Valid RoleDTO roleDTO){
        Role role = RoleConverter.fromDTO(roleDTO);
        RoleDTO createdRole = roleService.createRole(role);
        return ResponseEntity.created(linkTo(methodOn(RoleController.class).getRole(createdRole.getId())).toUri())
                .body(createdRole);
    }

    @PutMapping(path = "/{roleId}")
    public ResponseEntity<?> updateRole(@RequestBody @Valid RoleDTO roleDTO,
                                        @PathVariable Long roleId){
        Role role = RoleConverter.fromDTO(roleDTO);
        RoleDTO updatedRole = roleService.updateRole(role, roleId);
        return ResponseEntity.created(linkTo(methodOn(RoleController.class).getRole(updatedRole.getId())).toUri())
                .body(updatedRole);
    }

    @DeleteMapping(path = "/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId){
        roleService.deleteRoleById(roleId);
        return ResponseEntity.noContent().build();
    }
}
