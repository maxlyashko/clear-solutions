package ua.lyashko.clear.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lyashko.clear.entity.User;
import ua.lyashko.clear.service.UserService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody User user) {
        List<String> validationErrors = validateUser(user);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(validationErrors);
        }
        User createdUser = userService.createUser(user);
        URI location = URI.create("/api/users/" + createdUser.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        List<String> validationErrors = validateUser(updatedUser);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return userService.updateUser(id, updatedUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updatePartialUser(@PathVariable Long id, @RequestBody User partialUser) {
        return userService.updatePartialUser(id, partialUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUserByBirthDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        return ResponseEntity.ok(userService.searchUsersByBirthDateRange(from, to));
    }

    private List<String> validateUser(User user) {
        List<String> validationErrors = new ArrayList<>();
        if (isEmptyOrNull(user.getEmail()) || !isValidEmail(user.getEmail())) {
            validationErrors.add("Invalid email address.");
        }
        if (isEmptyOrNull(user.getFirstName())) {
            validationErrors.add("First name is required.");
        }
        if (isEmptyOrNull(user.getLastName())) {
            validationErrors.add("Last name is required.");
        }
        if (user.getBirthDate()==null || !isPastDate(user.getBirthDate())) {
            validationErrors.add("Invalid birth date or date in the future.");
        }
        return validationErrors;
    }

    private boolean isEmptyOrNull(String str) {
        return str==null || str.isEmpty();
    }

    private boolean isValidEmail(String email) {
        return email!=null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isPastDate(Date date) {
        return date!=null && date.before(new Date());
    }
}
