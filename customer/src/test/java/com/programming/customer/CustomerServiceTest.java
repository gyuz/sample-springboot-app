package com.programming.customer;

import com.programming.customer.dto.CustomerDTO;
import com.programming.customer.persistence.model.Customer;
import com.programming.customer.persistence.repository.CustomerRepository;
import com.programming.customer.service.CustomerServiceImpl;
import com.programming.customer.util.DozerMapperUtil;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DozerMapperUtil dozerMapperUtil;

    @Mock
    private DozerBeanMapper dozerBeanMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setup() {
        customerService = new CustomerServiceImpl(customerRepository, dozerMapperUtil);
    }

    @Test
    void FindAllCustomer_Empty_Success() {
        List<Customer> customerList = new ArrayList<>();
        when(customerRepository.findAll()).thenReturn(customerList);
        List<CustomerDTO> returnedList = customerService.findCustomers();
        assertThat(returnedList.size()).isZero();
    }

    @Test
    void FindAllCustomer_NonEmpty_Success() {
        List<Customer> customerList = Collections.singletonList(new Customer());
        when(dozerMapperUtil.mapper()).thenReturn(dozerBeanMapper);
        when(customerRepository.findAll()).thenReturn(customerList);

        List<CustomerDTO> returnedList = customerService.findCustomers();
        assertThat(returnedList.size()).isEqualTo(1);
    }

    @Test
    void FindCustomerById_Existing_Success() {
        when(dozerBeanMapper.map(any(Customer.class), any())).thenReturn(new CustomerDTO());
        when(dozerMapperUtil.mapper()).thenReturn(dozerBeanMapper);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

        List<CustomerDTO> customerDTOList = customerService.findCustomerById(1L);
        assertThat(customerDTOList).isNotEmpty();
    }


    @Test
    void FindCustomerById_NonExisting_Empty() {
        when(customerRepository.findById(8L)).thenReturn(Optional.empty());

        List<CustomerDTO> customerDTOList = customerService.findCustomerById(8L);
        assertThat(customerDTOList).isEmpty();
    }

    @Test
    void CountCustomerWithId_Existing_NotZero() {
        when(customerRepository.countCustomerwithId(1L)).thenReturn(1);

        int customerCount = customerService.countCustomerWithId(1L);
        assertThat(customerCount).isNotZero();
    }


    @Test
    void CountCustomerWithId_NonExisting_Zero() {
        when(customerRepository.countCustomerwithId(100L)).thenReturn(0);

        int customerCount = customerService.countCustomerWithId(100L);
        assertThat(customerCount).isZero();
    }

    @Test
    void SaveCustomer_NewCustomer_CreationSuccess() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setMiddleName("Smith");
        customer.setLastName("Doe");
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith", "Doe");

        when(dozerBeanMapper.map(any(Customer.class), any())).thenReturn(customerDTO);
        when(dozerBeanMapper.map(any(CustomerDTO.class), any())).thenReturn(customer);
        when(dozerMapperUtil.mapper()).thenReturn(dozerBeanMapper);
        when(customerRepository.save(any())).thenReturn(customer);

        CustomerDTO returnedDTO = customerService.saveCustomer(customerDTO);
        assertThat(returnedDTO.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(returnedDTO.getMiddleName()).isEqualTo(customer.getMiddleName());
        assertThat(returnedDTO.getLastName()).isEqualTo(customer.getLastName());
    }

    @Test
    void UpdateCustomer_ExistingCustomer_CreationSuccess() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Johnny");
        customer.setMiddleName("Smith");
        customer.setLastName("Doe");
        CustomerDTO customerDTO = new CustomerDTO(1L, "Johnny", "Smith", "Doe");

        when(dozerBeanMapper.map(any(Customer.class), any())).thenReturn(customerDTO);
        when(dozerBeanMapper.map(any(CustomerDTO.class), any())).thenReturn(customer);
        when(dozerMapperUtil.mapper()).thenReturn(dozerBeanMapper);
        when(customerRepository.save(any())).thenReturn(customer);

        CustomerDTO returnedDTO = customerService.saveCustomer(customerDTO);
        assertThat(returnedDTO.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(returnedDTO.getMiddleName()).isEqualTo(customer.getMiddleName());
        assertThat(returnedDTO.getLastName()).isEqualTo(customer.getLastName());
    }
}
