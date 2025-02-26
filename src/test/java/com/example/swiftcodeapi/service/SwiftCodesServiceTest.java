package com.example.swiftcodeapi.service;

import com.example.swiftcodeapi.dto.*;
import com.example.swiftcodeapi.model.Bank;
import com.example.swiftcodeapi.model.Country;
import com.example.swiftcodeapi.repository.BankRepo;
import com.example.swiftcodeapi.repository.CountryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwiftCodesServiceTest {
    @Mock
    private BankRepo bankRepo;

    @Mock
    private CountryRepo countryRepo;

    @InjectMocks
    private SwiftCodesService swiftCodesService;

    @Test
    public void isHeadquarters_swiftCodeEndsWithXXX_returnTrue() {
        assertTrue(SwiftCodesService.isHeadquarters("ABCDEFGHXXX"));
    }

    @Test
    public void isHeadquarters_swiftCodeDoesNotEndWithXXX_returnFalse() {
        assertFalse(SwiftCodesService.isHeadquarters("ABCDEFGHIJK"));
    }

    @Test
    public void getSwiftCodeDetails_swiftCodeIsHeadquarters_returnHeadquartersSwiftCodeResponse() {
        Bank bank = new Bank();
        bank.setSwiftCode("ABCDEFGHXXX");
        Country country = new Country();
        country.setIso2("FR");
        bank.setCountry(country);

        when(bankRepo.findBySwiftCode("ABCDEFGHXXX")).thenReturn(bank);

        assertInstanceOf(HeadquartersSwiftCodeResponse.class, swiftCodesService.getSwiftCodeDetails("ABCDEFGHXXX"));
    }

    @Test
    public void getSwiftCodeDetails_swiftCodeIsBranch_returnBranchSwiftCodeResponse() {
        Bank bank = new Bank();
        bank.setSwiftCode("ABCDEFGHIJK");
        Country country = new Country();
        country.setIso2("FR");
        bank.setCountry(country);

        when(bankRepo.findBySwiftCode("ABCDEFGHIJK")).thenReturn(bank);

        assertInstanceOf(BranchSwiftCodeResponse.class, swiftCodesService.getSwiftCodeDetails("ABCDEFGHIJK"));
    }

    @Test
    public void getSwiftCodeDetails_bankNotFound_returnFalse() {
        when(bankRepo.findBySwiftCode("Test123")).thenReturn(null);

        assertNull(swiftCodesService.getSwiftCodeDetails("Test123"));
    }

    @Test
    public void getSwiftCodesByCountry_countryFound() {
        Country country = new Country();
        country.setIso2("FR");

        Bank bank1 = new Bank();
        bank1.setCountry(country);
        bank1.setSwiftCode("ABCDEFGHXXX");

        Bank bank2 = new Bank();
        bank2.setCountry(country);
        bank2.setSwiftCode("ABCDEFGHIJK");

        when(countryRepo.findByIso2("FR")).thenReturn(country);
        when(bankRepo.findByCountry(country)).thenReturn(Arrays.asList(bank1, bank2));

        CountrySwiftCodeResponse response = swiftCodesService.getSwiftCodesByCountry("FR");

        assertNotNull(response);
        assertEquals("FR", response.getCountryISO2());
        assertEquals(2, response.getSwiftCodes().size());
    }

    @Test
    public void getSwiftCodesByCountry_countryNotFound_returnNull() {
        when(countryRepo.findByIso2("XX")).thenReturn(null);

        assertNull(swiftCodesService.getSwiftCodesByCountry("XX"));
    }

    @Test
    public void addSwiftCode_newSwiftCode_returnConfirmationMessageResponse() {
        SwiftCodeRequest request = new SwiftCodeRequest();
        request.setSwiftCode("Test123");
        request.setCountryISO2("FR");
        request.setCountryName("France");

        when(bankRepo.findBySwiftCode("Test123")).thenReturn(null);
        when(countryRepo.findByIso2("FR")).thenReturn(null);

        MessageResponse response = swiftCodesService.addSwiftCode(request);

        assertEquals("SWIFT code added successfully", response.getMessage());
    }

    @Test
    public void addSwiftCode_swiftCodeAlreadyExists_returnMessageResponseWithError() {
        SwiftCodeRequest request = new SwiftCodeRequest();
        request.setSwiftCode("Test123");

        when(bankRepo.findBySwiftCode("Test123")).thenReturn(new Bank());

        MessageResponse response = swiftCodesService.addSwiftCode(request);

        assertEquals("Error: SWIFT code already exists", response.getMessage());
    }

    @Test
    public void deleteSwiftCode_swiftCodeFound_returnConfirmationMessageResponse() {
        when(bankRepo.findBySwiftCode("Test123")).thenReturn(new Bank());

        MessageResponse response = swiftCodesService.deleteSwiftCode("Test123");

        assertEquals("SWIFT code deleted successfully", response.getMessage());
    }

    @Test
    public void deleteSwiftCode_swiftCodeNotFound_returnMessageResponseWithError() {
        when(bankRepo.findBySwiftCode("Test123")).thenReturn(null);

        MessageResponse response = swiftCodesService.deleteSwiftCode("Test123");

        assertEquals("Error: SWIFT code not found", response.getMessage());
    }
}