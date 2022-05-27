package com.mikep.applicantTimer.Services;

import com.mikep.applicantTimer.Models.Customer;
import com.mikep.applicantTimer.Models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;


//    public void setPasswordEncoder(PasswordEncoder pw) {
//        passwordEncoder = pw;
//    }
//    public PasswordEncoder getPasswordEncoder() {
//        return passwordEncoder;
//    }
    @Autowired
    private JavaMailSender mailSender;

    public Customer register(Customer customer, String siteURL) throws UnsupportedEncodingException,
            MessagingException {
        customer.setVerificationCode(UUID.randomUUID().toString());
        customer.setEnabled(false);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setConfirmPassword("");
        customer.setRoles(new HashSet(Arrays.asList(roleService.findByRoleName("ROLE_USER"))));
        customer.setCreatedAt(Instant.now());
        Customer savedCustomer = customerService.updateCustomer(customer);
        sendVerificationEmail(customer, siteURL);
        return savedCustomer;
    }
    public void resendVerificationEmail(Customer customer, String siteURL) throws
            MessagingException, UnsupportedEncodingException {
        sendVerificationEmail(customer, siteURL);
    }
    private void sendVerificationEmail(Customer customer, String siteURL) throws
            MessagingException, UnsupportedEncodingException {
        String verifyURL = siteURL + "/auth/verify?code=" + customer.getVerificationCode();

        String toAddress = customer.getUsername();
        String fromAddress = "mike.putnam@gmail.com";
        String senderName = "ReactApp";
        String subject = "Please verify your registration";
        String content = "Dear " + customer.getName() + ",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h2><a href=" + verifyURL + " target=\"_self\">Verify</a></h2>"
                + "Thank you,<br>"
                + "React App Team";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        Customer customer = customerService.findByVerificationCode(verificationCode);

        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customer.setVerificationCode(null);
            customer.setEnabled(true);
            customerService.updateCustomer(customer);

            return true;
        }

    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        Customer user_model = customerService.getCustomerByUsername(username);
        if (user_model == null) {
            throw new UsernameNotFoundException(username);
        }
        //List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user_model.getUsername(),
                user_model.getPassword(),
                getUserAuthority(user_model.getRoles()));
    }

    public boolean isAdmin(String username) {
        Set<Role> roles = customerService.getCustomerByUsername(username).getRoles();
        for(Role role : roles) {
            if(role.getRoleName().contains("ROLE_ADMIN")) return true;
        }
        return false;
    }
    public List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.getRoleName()));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }
}
