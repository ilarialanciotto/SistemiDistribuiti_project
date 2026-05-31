package org.ilaria.progettosistemidistribuiti;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.Entity.User;
import org.ilaria.progettosistemidistribuiti.Model.Role;
import org.ilaria.progettosistemidistribuiti.Repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class Initializer {

    private final AuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.setup.password}")
    private String AdminPassword;

    @PostConstruct
    public void initData() {
        if (userRepository.count() == 0) {
            String password = passwordEncoder.encode(AdminPassword);
            User admin = new User(null, "admin", "admin@gmail.com", password, Role.admin.name());
            admin.setRole(Role.admin.name());
            userRepository.save(admin);
        }
    }

}
