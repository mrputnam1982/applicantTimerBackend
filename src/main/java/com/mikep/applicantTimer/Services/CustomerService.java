package com.mikep.applicantTimer.Services;

import com.mikep.applicantTimer.Models.Customer;
import com.mikep.applicantTimer.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    public Customer getCustomerById(String id) {
        return customerRepository.findById(id).get();
    }

    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomerById(String id) {
        customerRepository.deleteById(id);
    }
    public Customer findByVerificationCode(String verificationCode){
        return customerRepository.findByVerificationCode(verificationCode);
    }
}
