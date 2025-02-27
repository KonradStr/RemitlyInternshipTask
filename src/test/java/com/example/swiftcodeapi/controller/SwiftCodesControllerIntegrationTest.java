package com.example.swiftcodeapi.controller;

import com.example.swiftcodeapi.dto.SwiftCodeRequest;
import com.example.swiftcodeapi.service.SwiftCodesService;
import com.example.swiftcodeapi.repository.CountryRepo;
import com.example.swiftcodeapi.repository.BankRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SwiftCodesControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static public void setUp(@Autowired SwiftCodesService service, @Autowired BankRepo bankRepo, @Autowired CountryRepo countryRepo) {
        bankRepo.deleteAll();
        countryRepo.deleteAll();
        service.importData("Interns_2025_SWIFT_CODES.xlsx");
    }

    @Test
    public void getSwiftCodeDetails_swiftCodeFound_returnOK() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/AAISALTRXXX"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.swiftCode").value("AAISALTRXXX"));
    }

    @Test
    public void getSwiftCodeDetails_swiftCodeNotFound_returnNotFound() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/AAAAAAAAAAA"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getSwiftCodesByCountry_found_returnOk() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/country/BG"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.countryISO2").value("BG"));
    }

    @Test
    public void getSwiftCodesByCountry_notFound_returnNotFound() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/country/XX"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addSwiftCode_success_returnCreated() throws Exception {
        SwiftCodeRequest request = new SwiftCodeRequest("Test address", "Test bank", "XX", "Test country", true, "TESTTESTXXX");

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SWIFT code added successfully"));
    }

    @Test
    public void addSwiftCode_failure_returnBadRequest() throws Exception { // failure due to swift code consisting of insufficient number of characters
        SwiftCodeRequest request = new SwiftCodeRequest("Test address", "Test bank", "XX", "Test country", true, "X");

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteSwiftCode_success_returnOk() throws Exception {
        mockMvc.perform(delete("/v1/swift-codes/ADCRBGS1XXX"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SWIFT code deleted successfully"));

    }

    @Test
    public void deleteSwiftCode_notFound_returnNotFound() throws Exception {
        mockMvc.perform(delete("/v1/swift-codes/AAAAAAAAAAA"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
