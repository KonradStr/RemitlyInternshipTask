package com.example.swiftcodeapi.dto;

import com.example.swiftcodeapi.model.Bank;

public class BranchSwiftCodeResponse extends SwiftCodeResponse {
    public BranchSwiftCodeResponse(Bank bank) {
        setAddress(bank.getAddress());
        setBankName(bank.getName());
        setCountryISO2(bank.getCountry().getIso2());
        setCountryName(bank.getCountry().getName());
        setIsHeadquarter(false);
        setSwiftCode(bank.getSwiftCode());
    }
}
