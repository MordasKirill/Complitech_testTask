package com.complitech.demo.controller;

import com.complitech.demo.entity.User;
import com.complitech.demo.entity.UserAction;
import com.complitech.demo.entity.UserDTO;
import com.complitech.demo.exception.UserNotFoundException;
import com.complitech.demo.service.UserService;
import com.complitech.demo.service.UserServiceClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserServiceClient userServiceClient;

    public UserController(UserService userService,
                          SimpMessagingTemplate messagingTemplate,
                          UserServiceClient userServiceClient) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
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
        users.stream()
                .filter(Objects::nonNull)
                .forEach(user ->
                        messagingTemplate.convertAndSend("/app/chat",
                                new UserAction(user, "use request GET /users"))
                );
        return userService.getAllUsers();
    }

    @PostMapping("/test")
    public void createUserUsingClient(@Valid @RequestBody UserDTO userDTO) throws Exception {
        userServiceClient.createUserRequest(userDTO);
    }
}
