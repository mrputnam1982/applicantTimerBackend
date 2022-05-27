package com.mikep.applicantTimer.Repositories;

import com.mikep.applicantTimer.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRoleName(String roleName);
}