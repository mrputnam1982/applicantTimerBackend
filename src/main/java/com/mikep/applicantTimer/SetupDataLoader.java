package com.mikep.applicantTimer;

import com.mikep.applicantTimer.Models.Customer;
import com.mikep.applicantTimer.Models.Privilege;
import com.mikep.applicantTimer.Models.Role;
import com.mikep.applicantTimer.Repositories.CustomerRepository;
import com.mikep.applicantTimer.Repositories.PrivilegeRepository;
import com.mikep.applicantTimer.Repositories.RoleRepository;
import com.mikep.applicantTimer.Models.Customer;
import com.mikep.applicantTimer.Models.Privilege;
import com.mikep.applicantTimer.Models.Role;
import com.mikep.applicantTimer.Repositories.CustomerRepository;
import com.mikep.applicantTimer.Repositories.PrivilegeRepository;
import com.mikep.applicantTimer.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    boolean alreadySetup = false;

    @Value("${spring.data.admin_password}")
    private String password;

    @Value("${spring.data.admin_username}")
    private String username;



    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege removePrivilege
                = createPrivilegeIfNotFound("REMOVE_PRIVILEGE");
        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege, removePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_GUEST", Arrays.asList(readPrivilege));

        if(customerRepository.findByUsername(username) == null) {
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN");
            Customer customer = new Customer();
            customer.setName("Admin");
            customer.setEnabled(true);
            customer.setPassword(passwordEncoder.encode(password));

            customer.setUsername(username);
            customer.setRoles(new HashSet(Arrays.asList(adminRole)));
            customerRepository.save(customer);
        }
        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByPrivilegeName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByRoleName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
