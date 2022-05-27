package com.mikep.applicantTimer.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Privileges")
public class Privilege {

    public Privilege(String privilegeName) {
        this.privilegeName = privilegeName;
    }
    @Id
    private String id;

    private String privilegeName;
}