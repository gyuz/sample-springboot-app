package com.programming.dashboard.service;

import com.programming.dashboard.dto.CustomerDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Data
public class CustomerServiceImpl implements CustomerService {

    @Value("${customer.app.uri}")
    private String customerAppUri;

    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<List<CustomerDTO>> findCustomers() {
        log.info("Fetching customers.");
            ResponseEntity<CustomerDTO[]> responseEntity = restTemplate.getForEntity(customerAppUri.concat("/all"), CustomerDTO[].class);
            return ResponseEntity.ok().body(Arrays.asList(Objects.requireNonNull(responseEntity.getBody())));
    }

    @Override
    public ResponseEntity<List<CustomerDTO>> findCustomerById(Long customerId) {
        log.info("Fetching customer with id {}", customerId);
        ResponseEntity<CustomerDTO[]> responseEntity = restTemplate.getForEntity(customerAppUri.concat("/{id}"), CustomerDTO[].class, Long.toString(customerId));
        return ResponseEntity.ok().body(Arrays.asList(Objects.requireNonNull(responseEntity.getBody())));
    }

    @Override
    public ResponseEntity<CustomerDTO> saveCustomer(CustomerDTO customerDTO) {
        log.info("Creating new customer. id={}, first_name={}, middle_name={}, last_name={}",
                customerDTO.getId(), customerDTO.getFirstName(), customerDTO.getMiddleName(), customerDTO.getLastName());
        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        ResponseEntity<CustomerDTO> responseEntity = restTemplate.exchange(customerAppUri.concat("/save"), HttpMethod.POST, request, CustomerDTO.class);
        return ResponseEntity.ok().body(responseEntity.getBody());
    }

    @Override
    public ResponseEntity<CustomerDTO> updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating customer. id={}, first_name={}, middle_name={}, last_name={}",
                customerDTO.getId(), customerDTO.getFirstName(), customerDTO.getMiddleName(), customerDTO.getLastName());
        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        ResponseEntity<CustomerDTO> responseEntity = restTemplate.exchange(customerAppUri.concat("/update/{id}"),
                HttpMethod.PUT, request, CustomerDTO.class, String.valueOf(customerDTO.getId()));
        return ResponseEntity.ok().body(responseEntity.getBody());
    }
}
