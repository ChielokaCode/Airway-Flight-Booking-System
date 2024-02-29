package org.chielokacode.airwaycc.airwaybackendcc.boot;


import jakarta.annotation.PostConstruct;

import org.chielokacode.airwaycc.airwaybackendcc.config.SeedProperties;
import org.chielokacode.airwaycc.airwaybackendcc.enums.Role;
import org.chielokacode.airwaycc.airwaybackendcc.model.User;
import org.chielokacode.airwaycc.airwaybackendcc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminDataLoader {
    private final UserRepository userRepository;
    private final SeedProperties seedProperties;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminDataLoader(UserRepository userRepository, SeedProperties seedProperties, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.seedProperties = seedProperties;
        this.passwordEncoder = passwordEncoder;
    }
    private  List<User> adminList;

    @PostConstruct
    private void seedProperties(){
        if (seedProperties.isEnabled()){
            seedAdmin();
        }
    }

    public void seedAdmin() {
        adminList = userRepository.findUserByUserRole(Role.ADMIN);
        if ((long) adminList.size() == 0) {
            List<User> adminData = Arrays.asList(
                    new User("Chieloka", "Codes", "realmatec01@gmail.com", "08148057104", passwordEncoder.encode("1234"), Role.ADMIN, true),
                    new User("Eloka", "Madubugwu", "elokamadubugwu7@gmail.com", "08148057104", passwordEncoder.encode("1234"), Role.ADMIN, true)
            );

            adminData.stream()
                    .filter(user -> !containsEmail(adminList, user.getEmail()))
                    .forEach(userRepository::save);
        }
    }

        private boolean containsEmail (List < User > ListOfAdmin, String email){
            return ListOfAdmin.stream()
                    .anyMatch(user -> user.getEmail().equals(email));
        }
    }
