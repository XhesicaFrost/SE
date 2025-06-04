package com.blm.takeout.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.blm.takeout.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhonenumber(String phonenumber);
    Optional<User> findByUsernameOrPhonenumber(String username, String phonenumber);
    Optional<User> findByPhonenumberAndRole(String phonenumber, User.Role role);
    Boolean existsByUsername(String username);
    Boolean existsByPhonenumber(String phonenumber);
    Boolean existsByPhonenumberAndRole(String phonenumber, User.Role role);
}