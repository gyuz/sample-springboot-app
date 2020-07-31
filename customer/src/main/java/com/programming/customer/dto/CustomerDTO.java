package com.programming.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor @NoArgsConstructor
public class CustomerDTO {
    private Long id;

    @NotBlank(message = "First Name must not be blank")
    @Size(max = 150, message = "First Name must not exceed 150 characters")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last Name must not be blank")
    @Size(max = 150, message = "Last Name must not exceed 150 characters")
    private String lastName;
}
