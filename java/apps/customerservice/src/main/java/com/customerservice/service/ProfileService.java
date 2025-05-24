package com.customerservice.service;

import com.customerservice.exceptions.InvalidUsernameException;
import com.customerservice.model.Customer;
import com.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CustomerRepository customerRepository;


    public Customer getCustomerByUsername(String username)
            throws Exception {
        return this.customerRepository.findCustomerByUsername(username)
                .orElseThrow(() -> new InvalidUsernameException("Customer profile not found"));
    }

}
