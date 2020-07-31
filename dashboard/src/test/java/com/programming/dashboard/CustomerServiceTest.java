package com.programming.dashboard;

import com.programming.dashboard.dto.CustomerDTO;
import com.programming.dashboard.service.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private RestTemplate restTemplate;

    private static String url = "http://localhost:8081/api/customer";

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(restTemplate);
        customerService.setCustomerAppUri(url);
    }

    @Test
    void FindCustomers_Success() {
        CustomerDTO[] customerArr = new CustomerDTO[] {new CustomerDTO(1L, "John", "Smith", "Doe")};
        when(restTemplate.getForEntity(url.concat("/all"), CustomerDTO[].class)).thenReturn(ResponseEntity.ok().body(customerArr));

        ResponseEntity responseEntity = customerService.findCustomers();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void FindCustomerById_Existing_Success() {
        CustomerDTO[] customerArr = new CustomerDTO[] {new CustomerDTO(1L, "John", "Smith", "Doe")};
        when(restTemplate.getForEntity(url.concat("/{id}"), CustomerDTO[].class, "1")).thenReturn(ResponseEntity.ok().body(customerArr));

        ResponseEntity responseEntity = customerService.findCustomerById(1L);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void FindCustomerById_NonExisting_ThrowException() {
        when(restTemplate.getForEntity(url.concat("/{id}"), CustomerDTO[].class, "100"))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "404 NOT_FOUND", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.findCustomerById(100L));
        assertTrue(exception.getMessage().contains("404"));
    }

    @Test
    void SaveCustomer_ValidRequest_Success() {
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith", "Doe");
        CustomerDTO newCustomerDTO = new CustomerDTO(1L, "John", "Smith", "Doe");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/save"), HttpMethod.POST, request, CustomerDTO.class))
                .thenReturn(ResponseEntity.ok().body(newCustomerDTO));

        ResponseEntity<CustomerDTO> responseEntity = customerService.saveCustomer(customerDTO);
        CustomerDTO savedCustomerDTO = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert savedCustomerDTO != null;
        assertThat(savedCustomerDTO.getId()).isNotNull();
    }

    @Test
    void UpdateCustomer_ValidRequest_Success() {
        CustomerDTO customerDTO = new CustomerDTO(1L, "Johnny", "Smith", "Doe");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/update/{id}"), HttpMethod.PUT, request, CustomerDTO.class, String.valueOf(customerDTO.getId())))
                .thenReturn(ResponseEntity.ok().body(customerDTO));

        ResponseEntity<CustomerDTO> responseEntity = customerService.updateCustomer(customerDTO);
        CustomerDTO savedCustomerDTO = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert savedCustomerDTO != null;
        assertThat(savedCustomerDTO.getId()).isEqualTo(customerDTO.getId());
        assertThat(savedCustomerDTO.getFirstName()).isEqualTo(customerDTO.getFirstName());
        assertThat(savedCustomerDTO.getMiddleName()).isEqualTo(customerDTO.getMiddleName());
        assertThat(savedCustomerDTO.getLastName()).isEqualTo(customerDTO.getLastName());
    }

    @Test
    void SaveCustomer_FirstNameBlank_ThrowException() {
        CustomerDTO customerDTO = new CustomerDTO(null, "", "Smith", "Doe");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/save"), HttpMethod.POST, request, CustomerDTO.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.saveCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void SaveCustomer_LastNameBlank_BadRequest() {
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith", "");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/save"), HttpMethod.POST, request, CustomerDTO.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.saveCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void SaveCustomer_FirstNameExceedCharLimit_BadRequest() {
        CustomerDTO customerDTO = new CustomerDTO(null, "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901234567890",
                "Smith", "Doe");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/save"), HttpMethod.POST, request, CustomerDTO.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.saveCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void SaveCustomer_LastNameExceedCharLimit_BadRequest() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "John", "Smith",
                "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890"
        );

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/save"), HttpMethod.POST, request, CustomerDTO.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.saveCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_FirstNameBlank_ThrowException() {
        CustomerDTO customerDTO = new CustomerDTO(1L, "", "Smith", "Doe");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/update/{id}"), HttpMethod.PUT, request, CustomerDTO.class, String.valueOf(customerDTO.getId())))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.updateCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_LastNameBlank_ThrowException() {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith", "");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/update/{id}"), HttpMethod.PUT, request, CustomerDTO.class, String.valueOf(customerDTO.getId())))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.updateCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_FirstNameExceedCharLimit_ThrowException() {
        CustomerDTO customerDTO = new CustomerDTO(1L, "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901234567890",
                "Smith", "Doe");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/update/{id}"), HttpMethod.PUT, request, CustomerDTO.class, String.valueOf(customerDTO.getId())))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.updateCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_LastNameExceedCharLimit_ThrowException() {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John", "Smith",
                "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890"
        );

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/update/{id}"), HttpMethod.PUT, request, CustomerDTO.class, String.valueOf(customerDTO.getId())))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.updateCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    void UpdateCustomer_NonExisting_ThrowException() {
        CustomerDTO customerDTO = new CustomerDTO(100L, "John", "Smith", "Doe");

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        when(restTemplate.exchange(url.concat("/update/{id}"), HttpMethod.PUT, request, CustomerDTO.class, String.valueOf(customerDTO.getId())))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "404 NOT_FOUND", null, null, null));

        Exception exception = assertThrows(HttpClientErrorException.class,
                () -> customerService.updateCustomer(customerDTO));
        assertTrue(exception.getMessage().contains("404"));
    }

}
