package com.example.swiftcodeapi.repository;

import com.example.swiftcodeapi.model.Bank;
import com.example.swiftcodeapi.model.Country;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BankRepoTest {
    @Autowired
    private BankRepo repo;

    @Autowired
    private EntityManager entityManager;


    private Country testCountry;

    @BeforeEach
    public void setUp() {
        testCountry = new Country();
        testCountry.setIso2("FR");
        testCountry.setName("France");
        entityManager.persist(testCountry);
        entityManager.flush();
        Bank bank = new Bank();
        bank.setSwiftCode("BNPAFRPPXXX");
        bank.setName("BNP PARIBAS");
        bank.setAddress("16 BOULEVARD DES ITALIENS");
        bank.setCountry(testCountry);
        entityManager.persist(bank);
        entityManager.flush();
    }

    @Test
    @Transactional
    public void save_newBank_bankIsSaved() {
        Country country = new Country();
        country.setIso2("TT");
        country.setName("TestCountry");
        entityManager.persist(country);
        entityManager.flush();
        Bank bank = new Bank();
        bank.setSwiftCode("TESTTESTXXX");
        bank.setName("Test Bank");
        bank.setAddress("Test Address");
        bank.setCountry(country);

        Bank savedBank = repo.save(bank);

        assertNotNull(savedBank);
        assertTrue(savedBank.getBankId() > 0);
    }

    @Test
    @Transactional
    public void findBySwiftCode_existingSwiftCode_returnsBank() {
        Bank foundBank = repo.findBySwiftCode("BNPAFRPPXXX");
        assertNotNull(foundBank);
        assertEquals("BNPAFRPPXXX", foundBank.getSwiftCode());
    }

    @Test
    @Transactional
    public void findBySwiftCode_nonExistingSwiftCode_returnsNull() {
        Bank foundBank = repo.findBySwiftCode("TEST_NOT_FOUND");
        assertNull(foundBank);
    }

    @Test
    @Transactional
    public void findBySwiftCodeStartingWith_matchingPrefix_returnsBanks() {
        List<Bank> banks = repo.findBySwiftCodeStartingWith("BNPAFRPP");
        assertEquals(1, banks.size());
        assertEquals("BNPAFRPPXXX", banks.get(0).getSwiftCode());
    }

    @Test
    @Transactional
    public void findByCountry_existingCountry_returnsBanks() {
        List<Bank> banks = repo.findByCountry(testCountry);
        assertEquals(1, banks.size());
        assertEquals("BNPAFRPPXXX", banks.get(0).getSwiftCode());
    }
}