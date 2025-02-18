package com.example.swiftcodeapi.repository;

import com.example.swiftcodeapi.model.Bank;
import com.example.swiftcodeapi.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepo extends JpaRepository<Bank, Integer> {
    Bank findBySwiftCode(String swiftCode);

    List<Bank> findBySwiftCodeStartingWith(String swiftCodeBankIdentification);

    List<Bank> findByCountry(Country country);
}
