package com.programming.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.dashboard.controller.CustomerDashboardController;
import com.programming.dashboard.dto.CustomerDTO;
import com.programming.dashboard.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerDashboardControllerTest {

    @Mock
    private CustomerService customerService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomerDashboardController customerDashboardController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        customerDashboardController = new CustomerDashboardController(customerService);
        mockMvc = MockMvcBuilders.standaloneSetup(customerDashboardController).build();
    }

    @Test
    void GetAllCustomers_Success() throws Exception {
        when(customerService.findCustomers()).thenReturn(ResponseEntity.ok().body(Collections.emptyList()));

        mockMvc.perform(get("/dashboard/customer/all"))
                .andExpect(status().isOk());

        ResponseEntity<List<CustomerDTO>> responseEntity = customerDashboardController.getAllCustomers();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void GetCustomerById_Existing_Success() throws Exception {
        when(customerService.findCustomerById(1L)).thenReturn(ResponseEntity.ok().body(Collections.emptyList()));

        mockMvc.perform(get("/dashboard/customer/{id}", 1L))
                .andExpect(status().isOk());

        ResponseEntity<List<CustomerDTO>> responseEntity = customerDashboardController.findCustomerById(1L);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void GetCustomerById_NonExisting_NotFound() throws Exception {
        when(customerService.findCustomerById(100L)).thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class, () -> customerDashboardController.findCustomerById(100L));
    }

    @Test
    void SaveCustomer_Valid_Success() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith", "Doe");
        CustomerDTO newCustomerDTO = new CustomerDTO(1L, "John", "Smith", "Doe");

        when(customerService.saveCustomer(customerDTO)).thenReturn(ResponseEntity.ok().body(newCustomerDTO));

        mockMvc.perform(post("/dashboard/customer/save")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk());

        ResponseEntity<CustomerDTO> responseEntity = customerDashboardController.createCustomer(customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getId()).isNotNull();
    }

    @Test
    void UpdateCustomer_Valid_Success() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith", "Doe");
        Long customerId = customerDTO.getId();

        when(customerService.updateCustomer(customerDTO)).thenReturn(ResponseEntity.ok().body(customerDTO));

        mockMvc.perform(put("/dashboard/customer/update/{id}", customerId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk());

        ResponseEntity<CustomerDTO> responseEntity = customerDashboardController.updateCustomer(customerId, customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void UpdateCustomer_NonExisting_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(100L, "John", "Smith", "Doe");

        when(customerService.updateCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "404 NOT_FOUND", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.updateCustomer(customerDTO.getId(), customerDTO));
        assertTrue(exception.getMessage().contains("404"));
    }

    @Test
    void SaveCustomer_FirstNameBlank_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "", "Smith", "Doe");

        when(customerService.saveCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.createCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void SaveCustomer_LastNameBlank_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith", "");

        when(customerService.saveCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.createCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void SaveCustomer_FirstNameExceedCharLimit_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "12345678901234567890123456789012345678901234567890" +
                                                                    "12345678901234567890123456789012345678901234567890" +
                                                                    "123456789012345678901234567890123456789012345678901234567890",
                                                        "Smith", "Doe");

        when(customerService.saveCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.createCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void SaveCustomer_LastNameExceedCharLimit_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "Smith", "Doe",
                                            "12345678901234567890123456789012345678901234567890" +
                                            "12345678901234567890123456789012345678901234567890" +
                                            "123456789012345678901234567890123456789012345678901234567890"
                                            );

        when(customerService.saveCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.createCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_FirstNameBlank_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "", "Smith", "Doe");

        when(customerService.updateCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.updateCustomer(customerDTO.getId(), customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_LastNameBlank_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith", "");

        when(customerService.updateCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.updateCustomer(customerDTO.getId(), customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_FirstNameExceedCharLimit_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901234567890",
                "Smith", "Doe");

        when(customerService.updateCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.updateCustomer(customerDTO.getId(), customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_LastNameExceedCharLimit_ThrowException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "Smith", "Doe",
                "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890"
        );

        when(customerService.updateCustomer(customerDTO))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerDashboardController.updateCustomer(customerDTO.getId(), customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }
}
