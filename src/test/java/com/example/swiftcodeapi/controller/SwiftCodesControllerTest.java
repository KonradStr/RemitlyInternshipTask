package com.example.swiftcodeapi.controller;

import com.example.swiftcodeapi.dto.*;
import com.example.swiftcodeapi.model.Bank;
import com.example.swiftcodeapi.model.Country;
import com.example.swiftcodeapi.service.SwiftCodesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(controllers = SwiftCodesController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SwiftCodesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SwiftCodesService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getSwiftCodeDetails_branchFound_returnOK() throws Exception {
        String swiftCode = "TESTTESTABC";
        Country country = new Country();
        country.setIso2("TT");
        country.setName("TestCountry");
        Bank bank = new Bank();
        bank.setSwiftCode(swiftCode);
        bank.setName("Test Bank");
        bank.setAddress("Test Address");
        bank.setCountry(country);
        BranchSwiftCodeResponse expectedResponse = new BranchSwiftCodeResponse(bank);

        given(service.getSwiftCodeDetails(swiftCode)).willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(get("/v1/swift-codes/{swift-code}", swiftCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.swiftCode").value(swiftCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isHeadquarter").value(false));
    }

    @Test
    public void getSwiftCodeDetails_headquartersFound_returnOK() throws Exception {
        String swiftCode = "TESTTESTXXX";
        Country country = new Country();
        country.setIso2("TT");
        country.setName("TestCountry");
        Bank bank = new Bank();
        bank.setSwiftCode(swiftCode);
        bank.setName("Test Bank");
        bank.setAddress("Test Address");
        bank.setCountry(country);
        HeadquartersSwiftCodeResponse expectedResponse = new HeadquartersSwiftCodeResponse(bank);

        List<BranchSwiftCodeResponse> branches = new ArrayList<>();
        Bank branchBank = new Bank();
        String branchSwiftCode = "TESTTESTABC";
        branchBank.setSwiftCode(branchSwiftCode);
        branchBank.setName("Branch Test Bank");
        branchBank.setAddress("Branch Test Address");
        branchBank.setCountry(country);
        branches.add(new BranchSwiftCodeResponse(branchBank));
        expectedResponse.setBranches(branches);

        given(service.getSwiftCodeDetails(swiftCode)).willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(get("/v1/swift-codes/{swift-code}", swiftCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.swiftCode").value(swiftCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isHeadquarter").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.branches[0].swiftCode").value(branchSwiftCode));
    }

    @Test
    public void getSwiftCodeDetails_notFound_returnNotFound() throws Exception {
        String swiftCode = "TESTTESTXXX";

        given(service.getSwiftCodeDetails(swiftCode)).willReturn(null);

        ResultActions response = mockMvc.perform(get("/v1/swift-codes/{swift-code}", swiftCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getSwiftCodesByCountry_found_returnOk() throws Exception {
        String iso2 = "XX";
        CountrySwiftCodeResponse expectedResponse = new CountrySwiftCodeResponse(iso2, "Test country", List.of(
                new SwiftCodeInfo("Test address", "Test bank", iso2, false, "TESTTESTABC")
        ));

        given(service.getSwiftCodesByCountry(iso2)).willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(get("/v1/swift-codes/country/{countryISO2code}", iso2)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.swiftCodes[0].countryISO2").value(iso2));
    }

    @Test
    public void getSwiftCodesByCountry_notFound_returnNotFound() throws Exception {
        String iso2 = "XX";

        given(service.getSwiftCodesByCountry(iso2)).willReturn(null);

        ResultActions response = mockMvc.perform(get("/v1/swift-codes/country/{countryISO2code}", iso2)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addSwiftCode_success_returnCreated() throws Exception {
        SwiftCodeRequest request = new SwiftCodeRequest("Test address", "Test bank", "XX", "Test country", true, "TESTTESTXXX");
        MessageResponse expectedResponse = new MessageResponse("SWIFT code added successfully");

        given(service.addSwiftCode(ArgumentMatchers.any())).willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(post("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SWIFT code added successfully"));
    }

    @Test
    public void addSwiftCode_failure_returnBadRequest() throws Exception {
        SwiftCodeRequest request = new SwiftCodeRequest("Test address", "Test bank", "XX", "Test country", true, "TESTTESTXXX");
        MessageResponse expectedResponse = new MessageResponse("Error: SWIFT code already exists");

        given(service.addSwiftCode(ArgumentMatchers.any())).willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(post("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error: SWIFT code already exists"));
    }

    @Test
    public void deleteSwiftCode_success_returnOk() throws Exception {
        String swiftCode = "TESTTESTXXX";
        MessageResponse expectedResponse = new MessageResponse("SWIFT code deleted successfully");

        given(service.deleteSwiftCode(swiftCode)).willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(delete("/v1/swift-codes/{swift-code}", swiftCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SWIFT code deleted successfully"));
    }

    @Test
    public void deleteSwiftCode_notFound_returnNotFound() throws Exception {
        String swiftCode = "TESTTESTXXX";
        MessageResponse expectedResponse = new MessageResponse("Error: SWIFT code not found");

        given(service.deleteSwiftCode(swiftCode)).willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(delete("/v1/swift-codes/{swift-code}", swiftCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error: SWIFT code not found"));
    }
}