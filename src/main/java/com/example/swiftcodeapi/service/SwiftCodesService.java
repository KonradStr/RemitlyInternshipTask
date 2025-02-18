package com.example.swiftcodeapi.service;

import com.example.swiftcodeapi.dto.*;
import com.example.swiftcodeapi.model.Bank;
import com.example.swiftcodeapi.model.Country;
import com.example.swiftcodeapi.repository.BankRepo;
import com.example.swiftcodeapi.repository.CountryRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SwiftCodesService {
    @Autowired
    private BankRepo bankRepo;

    @Autowired
    private CountryRepo countryRepo;

    @Transactional
    public void importData(String fileName) {
        if (countryRepo.count() != 0) {
            log.info("Data already imported. Skipping import from {}", fileName);
            return;
        }

        try {
            ClassPathResource resource = new ClassPathResource(fileName);

            if (!resource.exists()) {
                log.error("File not found: {}", fileName);
                return;
            }

            try (InputStream xlsxFile = resource.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile)) {
                Sheet sheet = workbook.getSheetAt(0);
                if (sheet == null) {
                    log.error("Sheet not found in Excel file");
                    throw new RuntimeException("Sheet not found in Excel file");
                }

                Map<String, Country> countryMap = new HashMap<>();

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // skip first row

                    String iso2 = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
                    String swiftCode = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
                    String name = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
                    String address = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
                    String countryName = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();

                    Country country = countryMap.get(iso2);
                    if (country == null) { // if new country
                        country = new Country();
                        country.setIso2(iso2);
                        country.setName(countryName);

                        country = countryRepo.save(country);
                        countryMap.put(iso2, country);
                    }

                    Bank bank = new Bank();
                    bank.setSwiftCode(swiftCode);
                    bank.setName(name);
                    bank.setAddress(address);
                    bank.setCountry(country);
                    bankRepo.save(bank);
                }
            }

        } catch (IOException e) {
            log.error("Error while importing data from a file: {}", fileName);
        }

    }

    public static boolean isHeadquarters(String swiftCode) {
        return swiftCode.endsWith("XXX");
    }

    public SwiftCodeResponse getSwiftCodeDetails(String swiftCode) {
        Bank bank = bankRepo.findBySwiftCode(swiftCode);

        if (bank == null) {
            return null;
        }

        if (isHeadquarters(swiftCode)) {
            HeadquartersSwiftCodeResponse hqResponse = new HeadquartersSwiftCodeResponse(bank);
            List<Bank> branches = bankRepo.findBySwiftCodeStartingWith(swiftCode.substring(0, 8));

            List<BranchSwiftCodeResponse> branchResponses = new ArrayList<>();

            for (Bank branch : branches) {
                if (!isHeadquarters(branch.getSwiftCode())) { // check if it's a branch to avoid adding headquarters to branches
                    branchResponses.add(new BranchSwiftCodeResponse(branch));
                }
            }
            hqResponse.setBranches(branchResponses);

            return hqResponse;
        } else {
            return new BranchSwiftCodeResponse(bank);
        }
    }

    public CountrySwiftCodeResponse getSwiftCodesByCountry(String countryISO2code) {
        Country country = countryRepo.findByIso2(countryISO2code);

        if (country == null) {
            return null;
        }

        List<Bank> countrySwiftCodes = bankRepo.findByCountry(country);

        CountrySwiftCodeResponse response = new CountrySwiftCodeResponse();
        response.setCountryIOS2(country.getIso2());
        response.setCountryName(country.getName());

        List<SwiftCodeInfo> swiftCodes = new ArrayList<>();
        for (Bank bank : countrySwiftCodes) {
            swiftCodes.add(new SwiftCodeInfo(bank));
        }

        response.setSwiftCodes(swiftCodes);

        return response;
    }

    @Transactional
    public MessageResponse addSwiftCode(SwiftCodeRequest request) {
        if (bankRepo.findBySwiftCode(request.getSwiftCode()) != null) {
            return new MessageResponse("Error: SWIFT code already exists");
        }

        Country country = countryRepo.findByIso2(request.getCountryISO2());

        if (country == null) {
            country = new Country();
            country.setIso2(request.getCountryISO2());
            country.setName(request.getCountryName());
            try {
                countryRepo.save(country);
                log.info("Country added to database: ISO2={}, Name={}", request.getCountryISO2(), request.getCountryName());
            } catch (Exception e) {
                log.error("Error saving country: {}", e.getMessage(), e);
                return new MessageResponse("Error: Database error - " + e.getMessage());
            }
        }

        Bank bank = new Bank();
        bank.setSwiftCode(request.getSwiftCode());
        bank.setName(request.getBankName());
        bank.setAddress(request.getAddress());
        bank.setCountry(country);

        try {
            bankRepo.save(bank);
            log.info("Bank added to database: SWIFT={}, Name={}, Country={}", request.getSwiftCode(), request.getBankName(), country.getIso2());
        } catch (Exception e) {
            log.error("Error saving bank: {}", e.getMessage(), e);
            return new MessageResponse("Error: Database error - " + e.getMessage());
        }

        return new MessageResponse("SWIFT code added successfully");
    }

    public MessageResponse deleteSwiftCode(String swiftCode) {
        Bank bank = bankRepo.findBySwiftCode(swiftCode);

        if (bank == null) {
            return new MessageResponse("Error: SWIFT code not found");
        }

        try {
            bankRepo.delete(bank);
        } catch (Exception e) {
            log.error("SWIFT code deletion error: {}", e.getMessage(), e);
            return new MessageResponse("Error: Database error - " + e.getMessage());
        }

        return new MessageResponse("SWIFT code deleted successfully");
    }
}
