package com.programming.customer.controller;

import com.programming.customer.dto.CustomerDTO;
import com.programming.customer.exception.CustomerNotFoundException;
import com.programming.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
@Slf4j
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/all")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(){
        log.info("Attempting to fetch all customers");
        return ResponseEntity.ok().body(customerService.findCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<CustomerDTO>> findCustomerById(@Min (value = 1) @PathVariable(value = "id") Long customerId)
            throws CustomerNotFoundException {
        log.info("Attempting to find customer with id={}", customerId);
        List<CustomerDTO> customerDTOList = customerService.findCustomerById(customerId);
        if (customerDTOList.isEmpty()) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }
        return ResponseEntity.ok().body(customerDTOList);
    }

    @PostMapping("/save")
    public ResponseEntity<CustomerDTO> saveCustomer(@Validated @RequestBody CustomerDTO customerDTO) {
        log.info("Attempting to save customer.");
        return ResponseEntity.ok().body(customerService.saveCustomer(customerDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@Min (value = 1) @PathVariable(value = "id") Long customerId,
            @Validated @RequestBody CustomerDTO customerDTO)
            throws CustomerNotFoundException {
        log.info("Attempting to update customer with id={}.", customerDTO.getId());
        int customerCount = customerService.countCustomerWithId(customerId);
        if (customerCount == 0) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }
        customerDTO.setId(customerId);
        return ResponseEntity.ok().body(customerService.saveCustomer(customerDTO));
    }
}
