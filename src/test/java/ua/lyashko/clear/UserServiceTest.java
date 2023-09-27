package ua.lyashko.clear;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.lyashko.clear.entity.User;
import ua.lyashko.clear.exception.ResourceNotFoundException;
import ua.lyashko.clear.repository.UserRepository;
import ua.lyashko.clear.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUser_ValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(new Date());

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("John", createdUser.getFirstName());
        assertEquals("Doe", createdUser.getLastName());
    }

    @Test
    public void testUpdateUser_ValidUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setFirstName("Old");
        existingUser.setLastName("User");
        existingUser.setBirthDate(new Date());

        User updatedUser = new User();
        updatedUser.setEmail("new@example.com");
        updatedUser.setFirstName("New");
        updatedUser.setLastName("User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        Optional<User> result = userService.updateUser(userId, updatedUser);

        assertTrue(result.isPresent());
        User savedUser = result.get();
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("New", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setEmail("new@example.com");
        updatedUser.setFirstName("New");
        updatedUser.setLastName("User");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(userId, updatedUser);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdatePartialUser_ValidUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setFirstName("Old");
        existingUser.setLastName("User");
        existingUser.setBirthDate(new Date());

        User partialUser = new User();
        partialUser.setEmail("new@example.com");
        partialUser.setFirstName("New");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        Optional<User> result = userService.updatePartialUser(userId, partialUser);

        assertTrue(result.isPresent());
        User savedUser = result.get();
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("New", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());
    }

    @Test
    public void testUpdatePartialUser_UserNotFound() {
        Long userId = 1L;
        User partialUser = new User();
        partialUser.setEmail("new@example.com");
        partialUser.setFirstName("New");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.updatePartialUser(userId, partialUser);

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteUser_ValidUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("test@example.com");
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setBirthDate(new Date());

        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    public void testSearchUsersByBirthDateRange_ValidDates() {
        Date from = new Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000);
        Date to = new Date();

        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());

        when(userRepository.findByBirthDateBetween(from, to)).thenReturn(userList);

        List<User> result = userService.searchUsersByBirthDateRange(from, to);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testSearchUsersByBirthDateRange_InvalidDates() {
        Date from = new Date();
        Date to = new Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000);

        assertThrows(IllegalArgumentException.class, () -> userService.searchUsersByBirthDateRange(from, to));
    }
}
