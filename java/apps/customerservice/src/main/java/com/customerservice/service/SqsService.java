package com.customerservice.service;

import com.customerservice.model.Customer;
import com.customerservice.repository.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsService {

    private final CustomerRepository customerRepository;

    @Transactional
    public void processMessage(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message);

        String username = jsonNode.get("username").asText();

        Optional<Customer> existingCustomer = customerRepository.findCustomerByUsername(username);

        if (existingCustomer.isPresent()) {
            log.info("Customer already exists with customer_id {}", existingCustomer.get().getId());
            return;
        }

        Customer newCustomer = Customer.builder()
                .name(jsonNode.get("name").asText())
                .email(jsonNode.get("email").asText())
                .phoneNumber(jsonNode.get("phone_number").asText())
                .username(username)
                .build();

        Customer savedCustomer = customerRepository.save(newCustomer);
        log.info("Customer stored with customer_id {}", savedCustomer.getId());

    }

}
