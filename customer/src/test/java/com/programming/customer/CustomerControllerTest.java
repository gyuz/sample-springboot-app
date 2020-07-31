package com.programming.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.customer.controller.CustomerController;
import com.programming.customer.dto.CustomerDTO;
import com.programming.customer.exception.CustomerNotFoundException;
import com.programming.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        customerController = new CustomerController(customerService);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void GetAllCustomers_Success() throws Exception {
        when(customerService.findCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/customer/all"))
                .andExpect(status().isOk());

        ResponseEntity<List<CustomerDTO>> responseEntity = customerController.getAllCustomers();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void GetCustomerById_Existing_Success() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith", "Doe");

        when(customerService.findCustomerById(1L)).thenReturn(Collections.singletonList(customerDTO));

        mockMvc.perform(get("/api/customer/{id}", 1L))
                .andExpect(status().isOk());

        ResponseEntity<List<CustomerDTO>> responseEntity = customerController.findCustomerById(1L);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void GetCustomerById_NonExisting_ThrowException() throws Exception {
        Exception exception = assertThrows(CustomerNotFoundException.class,
                () -> customerController.findCustomerById(100L));
        assertTrue(exception.getMessage().contains("Customer not found"));

        mockMvc.perform(get("/api/customer/{id}", 100L))
                .andExpect(status().isNotFound());
    }

    @Test
    void SaveCustomer_Valid_Success() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith", "Doe");

        when(customerService.saveCustomer(customerDTO)).thenReturn(customerDTO);

        mockMvc.perform(post("/api/customer/save")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk());

        ResponseEntity<CustomerDTO> responseEntity = customerController.saveCustomer(customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void UpdateCustomer_Valid_Success() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith", "Doe");
        Long customerId = customerDTO.getId();

        when(customerService.countCustomerWithId(1L)).thenReturn(1);
        when(customerService.saveCustomer(customerDTO)).thenReturn(customerDTO);

        mockMvc.perform(put("/api/customer/update/{id}", customerId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk());

        ResponseEntity<CustomerDTO> responseEntity = customerController.updateCustomer(customerId, customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void SaveCustomer_FirstNameBlank_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "", "Smith", "Doe");

        mockMvc.perform(post("/api/customer/save")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void SaveCustomer_LastNameBlank_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith", "");

        mockMvc.perform(post("/api/customer/save")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void SaveCustomer_FirstNameExceedCharLimit_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901234567890",
                "Smith", "Doe");

        mockMvc.perform(post("/api/customer/save")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void SaveCustomer_LastNameExceedCharLimit_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "Smith", "Doe",
                "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890"
        );

        mockMvc.perform(post("/api/customer/save")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void UpdateCustomer_FirstNameBlank_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "", "Smith", "Doe");

        mockMvc.perform(put("/api/customer/update/{id}", customerDTO.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void UpdateCustomer_LastNameBlank_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith", "");

        mockMvc.perform(put("/api/customer/update/{id}", customerDTO.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void UpdateCustomer_FirstNameExceedCharLimit_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901234567890",
                "Smith", "Doe");

        mockMvc.perform(put("/api/customer/update/{id}", customerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void UpdateCustomer_LastNameExceedCharLimit_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith",
                "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890"
        );

        mockMvc.perform(put("/api/customer/update/{id}", customerDTO.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void UpdateCustomer_NonExist_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(100L, "John", "Smith", "Doe");
        Long customerId = customerDTO.getId();

        when(customerService.countCustomerWithId(100L)).thenReturn(0);

        Exception exception = assertThrows(CustomerNotFoundException.class,
                () -> customerController.updateCustomer(customerId, customerDTO));
        assertTrue(exception.getMessage().contains("Customer not found"));

        mockMvc.perform(put("/api/customer/update/{id}", customerId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isNotFound());
    }
}
