package com.programming.customer.service;

import com.programming.customer.dto.CustomerDTO;
import com.programming.customer.persistence.model.Customer;
import com.programming.customer.persistence.repository.CustomerRepository;
import com.programming.customer.util.DozerMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final DozerMapperUtil dozerMapperUtil;

    @Transactional(readOnly = true)
    @Override
    public List<CustomerDTO> findCustomers() {
        log.info("Fetching all customers.");
        List<Customer> customerList = customerRepository.findAll();

        return customerList.isEmpty() ? new ArrayList<>() :
                customerList
                .stream()
                .map(this::customerToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CustomerDTO> findCustomerById(Long customerId){
        log.info("Fetching customer with id: {}", customerId);
        Optional<Customer> customer = customerRepository.findById(customerId);

        return customer.map(value -> Collections.singletonList(customerToCustomerDTO(value))).orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    @Override
    public int countCustomerWithId(Long customerId) {
        return customerRepository.countCustomerwithId(customerId);
    }

    @Transactional
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO){
        log.info("Saving customer id={}", customerDTO.getId());
        trimCustomerName(customerDTO);
        return customerToCustomerDTO(customerRepository.save(customerDtoToCustomer(customerDTO)));
    }

    private void trimCustomerName(CustomerDTO customerDTO) {
        customerDTO.setFirstName(customerDTO.getFirstName().trim());
        customerDTO.setMiddleName(customerDTO.getMiddleName().trim());
        customerDTO.setLastName(customerDTO.getLastName().trim());
    }

    private CustomerDTO customerToCustomerDTO(Customer customer){
        log.info("Mapping customer to customer DTO.");
        return dozerMapperUtil.mapper().map(customer, CustomerDTO.class);
    }

    private Customer customerDtoToCustomer(CustomerDTO customerDTO){
        log.info("Mapping customer DTO to customer.");
        return dozerMapperUtil.mapper().map(customerDTO, Customer.class);
    }
}
