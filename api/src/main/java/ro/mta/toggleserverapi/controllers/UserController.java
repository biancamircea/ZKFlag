package ro.mta.toggleserverapi.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.LoginRequestDTO;
import ro.mta.toggleserverapi.DTOs.UserDTO;
import ro.mta.toggleserverapi.DTOs.UsersResponseDTO;
import ro.mta.toggleserverapi.converters.UserConverter;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.services.UserService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserConverter userConverter;

    @GetMapping
    public ResponseEntity<?> getAllUsersWithRoles(){
//        returns users and roles
        UsersResponseDTO usersResponseDTO = userService.getAllUsers();
        return ResponseEntity.ok(usersResponseDTO);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId){
        UserDTO userDTO = userService.getUser(userId);
        return ResponseEntity.ok(userDTO);

    }
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO){
        User user = userConverter.fromDTO(userDTO);
        UserDTO savedUser = userService.createUser(user);
        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(savedUser.getId())).toUri())
                .body(savedUser);

    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDTO,
                                        @PathVariable Long userId){
        User user = userConverter.fromDTO(userDTO);
        UserDTO updatedUser = userService.updateUser(user, userId);
        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(updatedUser.getId())).toUri())
                .body(updatedUser);

    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();

    }
}
