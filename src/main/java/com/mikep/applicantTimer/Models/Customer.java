package com.mikep.applicantTimer.Models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mikep.applicantTimer.Annotations.Cascade;
import com.mikep.applicantTimer.Annotations.ValidPassword;
import com.mikep.applicantTimer.Utils.Base64Deserializer;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Transient;

import javax.persistence.Lob;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Document(collection  = "Customer")
@Getter
@Setter
public class Customer {
    @Id
    private String id;

    @NotNull(message = "Please enter your full name")
    @NotBlank(message = "Please enter your full name")
    private String name;
    @NotNull(message = "Please enter an email")
    @NotBlank(message = "Please enter an email")
    @Email(message = "Please enter a valid email")
    @Indexed(unique = true)
    private String username;
    @ValidPassword
    @NotNull(message = "Please enter a password")
    @NotBlank(message = "Please enter a password")
    private String password;
    @Transient
    private String confirmPassword;

    private String verificationCode;
    private boolean enabled;

    @DBRef
    @Cascade()
    private Attachment attachment;

    private String fileName;
    private Instant fileDownloadedAt;
    private Instant fileUploadedAt;
    private long timeToUpload;

    private boolean fileDownloaded;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @DBRef
    @Cascade()
    private Set<Role> roles;


}