package com.mikep.applicantTimer.Controllers;

import com.mikep.applicantTimer.Models.Attachment;
import com.mikep.applicantTimer.Models.Customer;
import com.mikep.applicantTimer.Models.Role;
import com.mikep.applicantTimer.ResponseData;
import com.mikep.applicantTimer.Services.AttachmentService;
import com.mikep.applicantTimer.Services.CustomerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins="http://localhost:8080")
@RequestMapping("/api")
@Log4j2
public class CustomerController {
    @Value("${spring.data.admin_username}")
    private String adminUsername;
    @Autowired
    CustomerService customerService;

    @Autowired
    AttachmentService attachmentService;
    @GetMapping("/customers/getById/{id}")
    public Customer getCustomer(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/customers/getByUsername/{username}")
    public Customer getCustomerByUsername(@PathVariable String username) {
        return customerService.getCustomerByUsername(username);
    }
    
    @GetMapping("/customers")
    public List<Customer> getCustomers() {

        List<Customer> customers = customerService.getAll();
        List<Customer> modified_clients = new ArrayList<>();
        for(Customer customer : customers) {
           customer.setPassword("");
           customer.setRoles(null);
           modified_clients.add(customer);
        }
        return modified_clients;
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        Customer currentCustomer = customerService.getCustomerById(id);
        if(customer.getName() != null)
            currentCustomer.setName(customer.getName());
        if(customer.getUsername() != null)
            currentCustomer.setUsername(customer.getUsername());
        if(customer.getPassword() != null)
            currentCustomer.setPassword(customer.getPassword());
        if(customer.getRoles() != null)
            currentCustomer.setRoles(customer.getRoles());

        currentCustomer = customerService.updateCustomer(currentCustomer);
        return ResponseEntity.ok(currentCustomer);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity deleteClientById(@PathVariable String id) {
        //System.out.println(id);
        customerService.deleteCustomerById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/downloadFile/{username}")
    public ResponseEntity downloadFileAdmin(@PathVariable String username) {
        Customer admin = customerService.getCustomerByUsername(adminUsername);
        Customer customer = customerService.getCustomerByUsername(username);
//        Attachment attachment = admin.getAttachment();

        //customer.setFileDownloadedAt(Instant.now());
        Attachment attachment = customer.getAttachment();

        //customerService.updateCustomer(customer);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.valueOf(attachment.getFileType()));
        header.setContentLength(attachment.getFileData().length);
        header.set("Content-Disposition", "attachment; filename=" + customer.getFileName());

        return new ResponseEntity<>(attachment.getFileData(), header, HttpStatus.OK);
    }
    @GetMapping("/customers/downloadFile/{username}")
    public ResponseEntity downloadFileApplicant(@PathVariable String username) {
        Customer admin = customerService.getCustomerByUsername(adminUsername);
        Customer customer = customerService.getCustomerByUsername(username);
//        Attachment attachment = admin.getAttachment();
        if(customer.isFileDownloaded() == false) {
            customer.setFileDownloadedAt(Instant.now());
            customer.setFileDownloaded(true);
            Attachment attachment = admin.getAttachment();

            customerService.updateCustomer(customer);
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.valueOf(attachment.getFileType()));
            header.setContentLength(attachment.getFileData().length);
            header.set("Content-Disposition", "attachment; filename=" + attachment.getFileName());
            return new ResponseEntity<>(attachment.getFileData(), header, HttpStatus.OK);
        }
        else return new ResponseEntity<String>("File already downloaded", HttpStatus.OK);


    }
    @PostMapping("/admin/uploadFile/{username}")
    public ResponseData uploadFileAsAdmin(@PathVariable String username,
                                            @RequestParam("file") MultipartFile file) throws Exception {

        Attachment attachment = null;
        String downloadURL;
        Customer customer = customerService.getCustomerByUsername(username);
        Set<Role> roles = customer.getRoles();
        boolean isAdmin = false;
        for(Role role : roles) {
            if(role.getRoleName().contains("ROLE_ADMIN")) isAdmin = true;
        }
        if(isAdmin) {
            attachment = attachmentService.updateAttachment(file, username);
            customer.setAttachment(attachment);
            customer.setFileUploadedAt(Instant.now());
            customer = customerService.updateCustomer(customer);
            downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(attachment.getId())
                    .toUriString();

            return new ResponseData(username,
                    attachment.getFileName(),
                    downloadURL,
                    file.getContentType(),
                    file.getSize()
            );
        }
        else return null;
    }
    @PostMapping("/customers/uploadFile/{username}")
    public ResponseEntity uploadFileById(@PathVariable String username,
                                         @RequestParam("file") MultipartFile file) throws Exception {
        Customer customer = customerService.getCustomerByUsername(username);
        Customer admin = customerService.getCustomerByUsername(adminUsername);
        if(admin.getAttachment() == null)
            return new ResponseEntity<String>("No file available for upload",
                    HttpStatus.BAD_REQUEST);
        if(!file.getOriginalFilename().contains(admin.getAttachment().getFileName()))
            return new ResponseEntity<String>("Upload filename must match download filename of " +
                    admin.getAttachment().getFileName(),
                    HttpStatus.BAD_REQUEST);
        Attachment attachment = attachmentService.updateAttachment(file, username);
        customer.setAttachment(attachment);
        customer.setFileUploadedAt(Instant.now());
        String[] fileNameAndExtension = admin.getAttachment().getFileName().split("\\.");
        String date = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").
                format(new Date(System.currentTimeMillis()));
        customer.setFileName(fileNameAndExtension[0] + "_" +
                customer.getName() + "_" +
                date +
                "." +
                fileNameAndExtension[1]);
        customer.setTimeToUpload(Duration.between(customer.getFileDownloadedAt(),
                customer.getFileUploadedAt()).getSeconds());
        customer = customerService.updateCustomer(customer);

        return new ResponseEntity<String>("File uploaded successfully", HttpStatus.OK);

    }
    @GetMapping("/customers/getElapsedTime/{username}")
    public long getElapsedTime(@PathVariable String username) {
        Customer customer = customerService.getCustomerByUsername(username);
        long timeElapsedSinceDownload = Duration.between(customer.getFileDownloadedAt(), Instant.now()).getSeconds();
        return timeElapsedSinceDownload;
    }
}
