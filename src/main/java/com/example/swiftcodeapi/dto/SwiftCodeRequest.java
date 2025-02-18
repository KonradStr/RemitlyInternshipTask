package com.example.swiftcodeapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCodeRequest {
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address is too long")
    private String address;

    @NotBlank(message = "Bank name is required")
    @Size(max = 255, message = "Bank name is too long")
    private String bankName;

    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    private String countryISO2;

    @NotBlank(message = "Country name is required")
    @Size(max = 255, message = "Country name is too long")
    private String countryName;

    @NotNull(message = "IsHeadquarter is required")
    private Boolean isHeadquarter;

    @NotBlank(message = "SWIFT code is required")
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}[A-Z0-9]{3}$", message = "Invalid SWIFT code format")
    private String swiftCode;
}
