package com.mikep.applicantTimer.Repositories;

import com.mikep.applicantTimer.Models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    public void deleteById(String id);
    public Customer findByUsername(String username);
    public Customer findByVerificationCode(String verificationCode);
}
