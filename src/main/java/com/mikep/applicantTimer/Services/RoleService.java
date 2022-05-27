package com.mikep.applicantTimer.Services;

import com.mikep.applicantTimer.Models.Role;
import com.mikep.applicantTimer.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;
    public Role findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
