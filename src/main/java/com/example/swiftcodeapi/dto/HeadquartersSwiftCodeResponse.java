package com.example.swiftcodeapi.dto;

import com.example.swiftcodeapi.model.Bank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HeadquartersSwiftCodeResponse extends SwiftCodeResponse {
    List<BranchSwiftCodeResponse> branches = new ArrayList<>();

    public HeadquartersSwiftCodeResponse(Bank bank) { // adds basic information without branches
        setAddress(bank.getAddress());
        setBankName(bank.getName());
        setCountryISO2(bank.getCountry().getIso2());
        setCountryName(bank.getCountry().getName());
        setIsHeadquarter(true);
        setSwiftCode(bank.getSwiftCode());
    }
}
