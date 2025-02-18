package com.example.swiftcodeapi.repository;

import com.example.swiftcodeapi.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepo extends JpaRepository<Country, Integer> {
    Country findByIso2(String iso2);
}
