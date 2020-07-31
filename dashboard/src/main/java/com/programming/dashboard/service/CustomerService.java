package com.programming.dashboard.service;

import com.programming.dashboard.dto.CustomerDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {

    ResponseEntity<List<CustomerDTO>> findCustomers();

    ResponseEntity<List<CustomerDTO>> findCustomerById(Long customerId);

    ResponseEntity<CustomerDTO> saveCustomer(CustomerDTO customerDTO);

    ResponseEntity<CustomerDTO> updateCustomer(CustomerDTO customerDTO);
}
