package com.example.swiftcodeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountrySwiftCodeResponse {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeInfo> swiftCodes;
}
