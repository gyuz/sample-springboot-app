package com.programming.customer.service;

import com.programming.customer.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> findCustomers();

    List<CustomerDTO> findCustomerById(Long customerId);

    int countCustomerWithId(Long customerId);

    CustomerDTO saveCustomer(CustomerDTO customerDTO);
}
