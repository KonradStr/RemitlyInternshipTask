package com.example.swiftcodeapi.controller;

import com.example.swiftcodeapi.dto.CountrySwiftCodeResponse;
import com.example.swiftcodeapi.dto.MessageResponse;
import com.example.swiftcodeapi.dto.SwiftCodeRequest;
import com.example.swiftcodeapi.dto.SwiftCodeResponse;
import com.example.swiftcodeapi.service.SwiftCodesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/v1/swift-codes")
public class SwiftCodesController {
    @Autowired
    private SwiftCodesService service;


    @GetMapping("/{swift-code}")
    public ResponseEntity<SwiftCodeResponse> getSwiftCodeDetails(@PathVariable("swift-code") String swiftCode) {
        log.info("");
        SwiftCodeResponse response = service.getSwiftCodeDetails(swiftCode);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<CountrySwiftCodeResponse> getSwiftCodesByCountry(@PathVariable String countryISO2code) {
        CountrySwiftCodeResponse response = service.getSwiftCodesByCountry(countryISO2code);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> addSwiftCode(@Valid @RequestBody SwiftCodeRequest request) {
        MessageResponse response = service.addSwiftCode(request);

        if (response.getMessage().startsWith("Error")) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (response.getMessage().contains("already exists")) {
                status = HttpStatus.CONFLICT;
            }
            log.warn("Error adding SWIFT code: {}", response.getMessage());
            return new ResponseEntity<>(response, status);
        }

        log.info("Successfully added SWIFT code: {}", request.getSwiftCode());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{swift-code}")
    public ResponseEntity<MessageResponse> deleteSwiftCode(@PathVariable("swift-code") String swiftCode) {
        MessageResponse response = service.deleteSwiftCode(swiftCode);

        if (response.getMessage().startsWith("Error")) {
            log.warn("Attempt to delete non-existing SWIFT code: {}", swiftCode);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        log.info("Successfully deleted SWIFT code: {}", swiftCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return new ResponseEntity<>(new MessageResponse(errorMessage), HttpStatus.BAD_REQUEST);
    }
}