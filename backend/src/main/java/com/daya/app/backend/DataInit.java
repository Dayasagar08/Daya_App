package com.daya.app.backend;

import com.daya.app.backend.entity.ERole;
import com.daya.app.backend.entity.Role;
import com.daya.app.backend.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final RoleRepo roleRepository;

    @Override
    @Transactional
    public void run(String... args) {

        for (ERole roleName : ERole.values()) {

            if (roleRepository.findByName(roleName).isEmpty()) {

                Role role = Role.builder()
                        .name(roleName)
                        .build();

                roleRepository.save(role);
            }
        }
    }
}