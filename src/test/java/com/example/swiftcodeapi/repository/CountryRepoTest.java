package com.example.swiftcodeapi.repository;

import com.example.swiftcodeapi.model.Country;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CountryRepoTest {
    @Autowired
    private CountryRepo repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void save_newBank_bankIsSaved() {
        Country country = new Country();
        country.setIso2("FR");
        country.setName("France");

        Country savedCountry = repo.save(country);

        assertNotNull(savedCountry);
        assertTrue(savedCountry.getCountryId() > 0);
        assertEquals("FR", savedCountry.getIso2());
        assertEquals("France", savedCountry.getName());
    }

    @Test
    @Transactional
    void findByIso2_existingIso2_returnsCountry() {
        Country country = new Country();
        country.setIso2("DE");
        country.setName("Germany");
        entityManager.persist(country);
        entityManager.flush();

        Country foundCountry = repo.findByIso2("DE");

        assertNotNull(foundCountry);
        assertEquals("Germany", foundCountry.getName());
    }

    @Test
    @Transactional
    void findByIso2_nonExistingIso2_returnsNull() {
        Country foundCountry = repo.findByIso2("XX");
        assertNull(foundCountry);
    }
}