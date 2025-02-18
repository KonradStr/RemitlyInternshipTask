package com.example.swiftcodeapi;

import com.example.swiftcodeapi.service.SwiftCodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SwiftCodeApiApplication implements CommandLineRunner {
    @Autowired
    private SwiftCodesService swiftCodesService;


    public static void main(String[] args) {
        SpringApplication.run(SwiftCodeApiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        swiftCodesService.importData("Interns_2025_SWIFT_CODES.xlsx");
    }
}
