package org.chielokacode.airwaycc.airwaybackendcc.repository;

import org.chielokacode.airwaycc.airwaybackendcc.enums.Role;
import org.chielokacode.airwaycc.airwaybackendcc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserDetails> findByEmail(String email);
    boolean existsByEmail(String email);

    User findUserByEmail(String email);

    List<User> findUserByUserRole(Role role);
}