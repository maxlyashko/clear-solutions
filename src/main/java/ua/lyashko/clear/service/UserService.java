package ua.lyashko.clear.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.lyashko.clear.entity.User;
import ua.lyashko.clear.exception.ResourceNotFoundException;
import ua.lyashko.clear.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${age.requirement}")
    private int ageRequirement;

    public User createUser(User user) {
        if (isUserEligible(user.getBirthDate())) {
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("User must be at least " + ageRequirement + " years old.");
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setBirthDate(updatedUser.getBirthDate());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            return userRepository.save(existingUser);
        });
    }

    public Optional<User> updatePartialUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            if (updatedUser.getEmail()!=null) existingUser.setEmail(updatedUser.getEmail());
            if (updatedUser.getFirstName()!=null) existingUser.setFirstName(updatedUser.getFirstName());
            if (updatedUser.getLastName()!=null) existingUser.setLastName(updatedUser.getLastName());
            if (updatedUser.getBirthDate()!=null) existingUser.setBirthDate(updatedUser.getBirthDate());
            if (updatedUser.getAddress()!=null) existingUser.setAddress(updatedUser.getAddress());
            if (updatedUser.getPhoneNumber()!=null) existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            return userRepository.save(existingUser);
        });
    }

    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }

    public List<User> searchUsersByBirthDateRange(Date from, Date to) {
        if (from.after(to)) {
            throw new IllegalArgumentException("From date must be before To date.");
        }
        return userRepository.findByBirthDateBetween(from, to);
    }

    private boolean isUserEligible(Date birthDate) {
        Date currentDate = new Date();
        long ageInMillis = currentDate.getTime() - birthDate.getTime();
        long ageInYears = ageInMillis / (1000L * 60 * 60 * 24 * 365);

        return ageInYears >= ageRequirement;
    }
}
