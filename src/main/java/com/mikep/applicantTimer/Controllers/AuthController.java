package com.mikep.applicantTimer.Controllers;

import com.mikep.applicantTimer.Exceptions.CustomInvalidKeyException;
import com.mikep.applicantTimer.Exceptions.UsernameExistsException;
import com.mikep.applicantTimer.Models.AuthenticationResponse;
import com.mikep.applicantTimer.Models.CustomCookie;
import com.mikep.applicantTimer.Models.Customer;
import com.mikep.applicantTimer.Models.Role;
import com.mikep.applicantTimer.Services.CustomerService;
import com.mikep.applicantTimer.Services.MyUserDetailsService;
import com.mikep.applicantTimer.Utils.JwtUtil;
import com.mikep.applicantTimer.Validators.MapValidationService;
import com.mikep.applicantTimer.Validators.UserValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;

@RestController
@CrossOrigin(origins="http://localhost:8080")
@RequestMapping("/auth/")
@Log4j2
public class AuthController {

    @Value("${spring.data.refresh_token_key}")
    private String refreshTokenKey;

    @Autowired
    CustomerService customerService;

    @Autowired
    MyUserDetailsService userDetailsService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    UserValidator userValidator;
    @Autowired
    MapValidationService mapValidationService;

    Key aesKey;
    @PutMapping("login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Customer customer)
            throws Exception {
        //log.info(client.getUsername());
        Customer savedCustomer  = customerService.getCustomerByUsername(customer.getUsername());

        if(savedCustomer == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);


        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    customer.getUsername(),
                    customer.getPassword()));
        } catch(BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        if(savedCustomer.isEnabled() == false)
            return new ResponseEntity<String>("Customer not enabled", HttpStatus.BAD_REQUEST);

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(savedCustomer.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        log.info(jwt);
        AuthenticationResponse authResponse = new AuthenticationResponse();
        authResponse.setUsername(savedCustomer.getName());
        authResponse.setJwt(jwt);
        authResponse.setRoles(savedCustomer.getRoles());
        //return ResponseEntity.ok(new AuthenticationResponse(jwt));
        return new ResponseEntity<AuthenticationResponse>(authResponse, HttpStatus.OK);


    }

    @PostMapping("refresh_token/generate")
    public ResponseEntity<Cookie> createRefreshToken(@RequestBody String username) {

        try {
            log.info(username);
            aesKey = new SecretKeySpec(refreshTokenKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(username.getBytes());
            Cookie refresh_token = new Cookie("refresh_token",
                    Base64.getEncoder().encodeToString(encrypted));
            refresh_token.setMaxAge(24 * 60 * 60);
            refresh_token.setHttpOnly(false);
            refresh_token.setSecure(true);
            return new ResponseEntity<>(refresh_token, HttpStatus.OK);
        } catch(NoSuchAlgorithmException |
                NoSuchPaddingException |
                IllegalBlockSizeException |
                BadPaddingException |
                InvalidKeyException e) {
            throw new CustomInvalidKeyException("Encryption of username with key "
                    + aesKey
                    + " failed");
        }
    }

    @PostMapping("refresh_token/update")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody CustomCookie cookie) {
        try {


            log.info(cookie);
            String username = cookie.getUsername();

            String cookieValue = cookie.getCookieValue();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decryptedVal = new String(cipher.doFinal(
                    Base64.getDecoder().decode(cookieValue)));
            if (decryptedVal.equals(username)) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Customer client = customerService.getCustomerByUsername(userDetails.getUsername());
                Set<Role> roles = client.getRoles();

                final String jwt = jwtTokenUtil.generateToken(userDetails);
                log.info("Created new User JWT token");
                return new ResponseEntity<>(new AuthenticationResponse(jwt,
                        userDetails.getUsername(),
                        roles), HttpStatus.OK);
            }

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } catch(NoSuchAlgorithmException |
                NoSuchPaddingException |
                IllegalBlockSizeException |
                BadPaddingException |
                InvalidKeyException e) {
            throw new CustomInvalidKeyException("Encryption of username with key "
                    + aesKey
                    + " failed");
        }
    }
    @PostMapping("reverify")
    public ResponseEntity<String> reverifyUser(@RequestBody Customer client,
                                               BindingResult result,
                                               HttpServletRequest request) {
        Customer existingClient = customerService.getCustomerByUsername(client.getUsername());
        try {
            userDetailsService.resendVerificationEmail(existingClient, getSiteURL(request));
        } catch(MessagingException | UnsupportedEncodingException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Resent verification email", HttpStatus.OK);

    }
    @PostMapping("register")
    public ResponseEntity<?> createUser(@Valid @RequestBody Customer customer,
                                        BindingResult result,
                                        HttpServletRequest request)
            throws URISyntaxException {
        //log.debug(client.toString());
        Customer temp = new Customer();
        temp = customer;
        userValidator.validate(temp, result);
        ResponseEntity<?> errorMap = mapValidationService.MapValidationSvc(result);
        if(errorMap != null) return errorMap;

        try{

            //client.setUsername(client.getUsername());

            Customer savedCustomer = userDetailsService.register(temp, getSiteURL(request));

            return new ResponseEntity<Customer>(savedCustomer,HttpStatus.CREATED);
        }
        catch(Exception e) {
            log.info(e.getMessage());
            if(e.getClass() == DuplicateKeyException.class)
                throw new UsernameExistsException(customer.getUsername() + " already exists!");
            else
                return new ResponseEntity<String>("Registration failed", HttpStatus.BAD_REQUEST);
        }
    }


    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
