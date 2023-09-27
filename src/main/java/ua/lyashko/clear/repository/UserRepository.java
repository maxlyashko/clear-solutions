package ua.lyashko.clear.repository;

import org.springframework.data.repository.CrudRepository;
import ua.lyashko.clear.entity.User;

import java.util.Date;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByBirthDateBetween(Date from, Date to);
}
