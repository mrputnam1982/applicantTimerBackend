package com.mikep.applicantTimer.Repositories;

import com.mikep.applicantTimer.Models.Privilege;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface PrivilegeRepository extends MongoRepository<Privilege, String> {
    public Privilege findByPrivilegeName(String privilegeName);
}
