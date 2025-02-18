package com.example.swiftcodeapi.dto;

import com.example.swiftcodeapi.model.Bank;
import com.example.swiftcodeapi.service.SwiftCodesService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCodeInfo {
    private String address;
    private String bankName;
    private String countryISO2;
    private Boolean isHeadquarter;
    private String swiftCode;

    public SwiftCodeInfo(Bank bank) {
        setAddress(bank.getAddress());
        setBankName(bank.getName());
        setCountryISO2(bank.getCountry().getIso2());
        setIsHeadquarter(SwiftCodesService.isHeadquarters(bank.getSwiftCode()));
        setSwiftCode(bank.getSwiftCode());
    }
}
