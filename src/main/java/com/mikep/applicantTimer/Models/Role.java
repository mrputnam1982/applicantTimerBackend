package com.mikep.applicantTimer.Models;

import com.mikep.applicantTimer.Annotations.Cascade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.IdClass;
import java.util.Collection;

@Document(collection="Roles")
@Getter
@Setter
public class Role {

    public Role(String roleName) {this.roleName = roleName;}
    @Id
    private String id;

    private String roleName;
    @DBRef
    @Cascade()
    private Collection<Privilege> privileges;



}