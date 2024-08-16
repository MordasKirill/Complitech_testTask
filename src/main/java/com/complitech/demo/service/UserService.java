package com.complitech.demo.service;

import com.complitech.demo.entity.Gender;
import com.complitech.demo.entity.User;
import com.complitech.demo.entity.UserAction;
import com.complitech.demo.entity.UserDTO;
import com.complitech.demo.exception.UserException;
import com.complitech.demo.repository.GenderRepository;
import com.complitech.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";

    /**
     * Constructor
     *
     * @param userRepository user repository instance
     * @param passwordEncoder password encoder instance
     * @param modelMapper model mapper instance
     * @param genderRepository gender repository instance
     */
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper,
                       GenderRepository genderRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.genderRepository = genderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Create new user in the system
     *
     * @param userDTO user DTO object
     * @return created user in the system, having assigned id
     */
    public User createUser(UserDTO userDTO) throws UserException {
        validateUserGender(userDTO.getGender().getId());
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Update existing user if such exist
     *
     * @param id id of user to be updated
     * @param userDTO used object containing updates
     * @return updated user object
     * @throws UserException if there is no such user in the system
     */
    public User updateUser(Long id, UserDTO userDTO) throws UserException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            log.error("User with id {} not found.", userDTO.getId());
            throw new UserException(String.format(USER_NOT_FOUND_MESSAGE, userDTO.getId()));
        }
        validateUserGender(userDTO.getGender().getId());
        User user = optionalUser.get();
        modelMapper.map(userDTO, user);

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    /**
     * Validates if provided gender id exists in the system
     *
     * @param genderId gender id
     */
    private void validateUserGender(long genderId) throws UserException {
        Optional<Gender> gender = genderRepository.findById(genderId);
        if (gender.isEmpty()) {
            log.error("Gender with id {} not found.", genderId);
            throw new UserException("Gender with id " + genderId + " not found.");
        }
    }

    /**
     * Delete single user from th system
     *
     * @param id user id to be deleted
     * @throws UserException is there is no such user id in the system
     */
    public void deleteUser(Long id) throws UserException {
        if (!userRepository.existsById(id)) {
            log.error("User with id {} not found, cannot delete.", id);
            throw new UserException(String.format(USER_NOT_FOUND_MESSAGE, id));
        }
        userRepository.deleteById(id);
        log.info("Users with id:{} deleted.", id);
    }

    /**
     * Delete users in provided range
     *
     * @param startId id from witch to delete
     * @param endId id until witch to delete
     */
    public void deleteUserInRange(Long startId, Long endId) {
        userRepository.deleteUsersInRange(startId, endId);
        log.info("Users from id:{}, to id:{} deleted.", startId, endId);
    }

    /**
     * Get single user by its id
     *
     * @param id user id to be received
     * @return user object
     * @throws UserException in case user not found in the system
     */
    public User getUser(Long id) throws UserException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            log.error("User with id {} not found.", id);
            throw new UserException(String.format(USER_NOT_FOUND_MESSAGE, id));
        }
        return optionalUser.get();
    }

    /**
     * Provide a List of all users in the system
     *
     * @return List of users objects
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username);
        if (Objects.isNull(user)) {
            log.error("User with login {} not found.", username);
        }
        return user;
    }

    /**
     * Send notification to /app/chat
     *
     * @param users List of users
     */
    public void sendNotificationToUsers(List<User> users) {
        users.stream()
                .filter(Objects::nonNull)
                .forEach(user ->
                        messagingTemplate.convertAndSend("/app/chat",
                                new UserAction(user, "use request GET /users"))
                );
    }
}
