CREATE TABLE countries (
                           country_id SERIAL PRIMARY KEY,
                           iso2 VARCHAR(2),
                           name VARCHAR(255)
);


CREATE TABLE banks (
                       bank_id SERIAL PRIMARY KEY,
                       swift_code VARCHAR(11),
                       name VARCHAR(255),
                       address VARCHAR(255),
                       country_id INTEGER,
                       FOREIGN KEY (country_id) REFERENCES countries(country_id)
);

CREATE INDEX banks_country_id_idx ON banks (country_id);

CREATE INDEX countries_iso2_idx ON countries (iso2);

CREATE INDEX banks_swift_code_idx ON banks (swift_code);