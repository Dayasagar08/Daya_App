package com.daya.app.backend.repo;

import com.daya.app.backend.entity.ERole;
import com.daya.app.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(ERole name);

}