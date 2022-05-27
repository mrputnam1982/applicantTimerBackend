package com.mikep.applicantTimer.Models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mikep.applicantTimer.Utils.Base64Deserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection  = "Attachment")
public class Attachment {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    private String username;
    private String fileName;
    private String fileType;
    @Lob
    private byte[] fileData;
    private String downloadURL;

    public Attachment(String username, String fileName, String fileType, byte[] fileData) {
        this.username = username;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
    }

}
