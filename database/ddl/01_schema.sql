CREATE DATABASE IF NOT EXISTS customer_db;
USE customer_db;

CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    date_of_birth DATE NOT NULL,
    nic_number VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_customer_nic (nic_number)
);

CREATE TABLE IF NOT EXISTS customer_mobile_numbers (
    customer_id BIGINT NOT NULL,
    mobile_number VARCHAR(20),
    CONSTRAINT fk_mobile_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS customer_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city_id BIGINT,
    country_id BIGINT,
    CONSTRAINT fk_address_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES cities(id),
    CONSTRAINT fk_address_country FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE IF NOT EXISTS customer_family_members (
    customer_id BIGINT NOT NULL,
    family_member_id BIGINT NOT NULL,
    PRIMARY KEY (customer_id, family_member_id),
    CONSTRAINT fk_family_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_family_member FOREIGN KEY (family_member_id) REFERENCES customers(id) ON DELETE CASCADE
);

