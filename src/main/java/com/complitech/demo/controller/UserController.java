package com.complitech.demo.controller;

import com.complitech.demo.entity.User;
import com.complitech.demo.entity.UserDTO;
import com.complitech.demo.exception.UserNotFoundException;
import com.complitech.demo.service.UserService;
import com.complitech.demo.service.UserServiceClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    private final UserServiceClient userServiceClient;

    public UserController(UserService userService,
                          UserServiceClient userServiceClient) {
        this.userService = userService;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) throws UserNotFoundException {
        userDTO.setId(id);
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUser(id);
    }

    @DeleteMapping("/range")
    public void deleteUsersInRange(@RequestParam Long idFrom, @RequestParam Long idTo) {
        userService.deleteUserInRange(idFrom, idTo);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) throws UserNotFoundException {
        return userService.getUser(id);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        userService.sendNotificationToUsers(users);
        return users;
    }

    @PostMapping("/test")
    public void createUserUsingClient(@Valid @RequestBody UserDTO userDTO) throws Exception {
        userServiceClient.createUserRequest(userDTO);
    }
}
