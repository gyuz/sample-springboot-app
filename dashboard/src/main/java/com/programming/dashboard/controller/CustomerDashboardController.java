package com.programming.dashboard.controller;

import com.programming.dashboard.dto.CustomerDTO;
import com.programming.dashboard.service.CustomerService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard/customer")
@Slf4j
public class CustomerDashboardController {

   private final CustomerService customerService;

    @GetMapping("/all")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(){
        log.info("Attempting to get all customers");
        return customerService.findCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<CustomerDTO>> findCustomerById(@PathVariable(value = "id") Long customerId) {
        log.info("Attempting to search customer with id={}", customerId);
        return customerService.findCustomerById(customerId);
    }

    @PostMapping("/save")
    public ResponseEntity<CustomerDTO> createCustomer(@Validated @RequestBody CustomerDTO customerDTO) {
        log.info("Attempting to create new customer. first_name={}, middle_name={}, last_name={}",
                customerDTO.getFirstName(), customerDTO.getMiddleName(), customerDTO.getLastName());
        return customerService.saveCustomer(customerDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable(value = "id") Long customerId,
                                                      @Validated @RequestBody CustomerDTO customerDTO) {
        log.info("Attempting to update customer with id={}", customerId);
        customerDTO.setId(customerId);
        return customerService.updateCustomer(customerDTO);
    }
}
