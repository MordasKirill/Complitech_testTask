package com.complitech.demo.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Login cannot be blank")
    @Size(max = 50, message = "Login cannot exceed 50 characters")
    private String login;

    @Size(min = 7, max = 20, message = "Password must be between 7 and 20 characters")
    @Pattern(regexp = "^(?=.*\\d{3,20})(?=.*[!@#$%^&*]).{1,20}$", message = "Password must contain at least 3 digits and 1 special character")
    private String password;

    @NotBlank(message = "Full name cannot be blank")
    @Size(max = 256, message = "Full name cannot exceed 256 characters")
    private String fullName;

    @NotNull
    private Gender gender;
}
