# SWIFT codes REST API

**Swift Code API** is a REST application that enables management of a SWIFT code database, allowing for searching,
adding, and deleting SWIFT codes

## Table of Contents

* [Main Technologies](#main-technologies)
* [Project Setup](#project-setup)
* [REST API Endpoints](#rest-api-endpoints)

## Main Technologies

* **Java**
* **Spring Boot**
* **PostgreSQL**
* **Docker**

### Included Java Libraries:

* Spring Web
* Spring Data JPA
* Hibernate
* Lombok
* Apache POI
* SLF4J
* Flyway
* JUnit

## Project Setup

### Prerequisites

* Java 21 or higher
* Maven
* Git
* PostgreSQL
* Docker and Docker Compose

### Installation and Configuration

1. **Clone the repository:**

```bash
git clone https://github.com/KonradStr/RemitlyInternshipTask.git
```

2. **Navigate to project directory:**

```bash
cd RemitlyInternshipTask
```

3. **Build the project:**

```bash
mvn clean install
```

4. **Run docker compose:**

```bash
docker-compose up -d
```

5. **Server will start on port 8080 and can be accessed at** `http://localhost:8080` **.**


6. **\*Running unit and integration tests:**

```bash
mvn test
```

## REST API Endpoints

### **Endpoint 1: Retrieve details of a single SWIFT code (headquarters or branch)**

* **GET:** `/v1/swift-codes/{swift-code}`

**Response structure for headquarters SWIFT code:**

```json
{
  "address": string,
  "bankName": string,
  "countryISO2": string,
  "countryName": string,
  "isHeadquarter": bool,
  "swiftCode": string
  "branches": [
    {
      "address": string,
      "bankName": string,
      "countryISO2": string,
      "isHeadquarter": bool,
      "swiftCode": string
    },
    {
      "address": string,
      "bankName": string,
      "countryISO2": string,
      "isHeadquarter": bool,
      "swiftCode": string
    },
    ...
  ]
}
```

**Response structure for branch SWIFT code:**

```json
{
  "address": string,
  "bankName": string,
  "countryISO2": string,
  "countryName": string,
  "isHeadquarter": bool,
  "swiftCode": string
}
```

### **Endpoint 2: Return all SWIFT codes for a specific country**

* **GET:** `/v1/swift-codes/country/{countryISO2code}`

**Response structure:**

```json
{
  "countryISO2": string,
  "countryName": string,
  "swiftCodes": [
    {
      "address": string,
      "bankName": string,
      "countryISO2": string,
      "isHeadquarter": bool,
      "swiftCode": string
    },
    {
      "address": string,
      "bankName": string,
      "countryISO2": string,
      "isHeadquarter": bool,
      "swiftCode": string
    },
    ...
  ]
}
```

### **Endpoint 3: Add a new SWIFT code entry to the database**

* **POST:** `/v1/swift-codes`

**Request structure:**

```json
{
  "address": string,
  "bankName": string,
  "countryISO2": string,
  "countryName": string,
  "isHeadquarter": bool,
  "swiftCode": string
}
```

**Response structure:**

```json
{
  "message": string
}
```

### **Endpoint 4: Delete a SWIFT code entry**

* **DELETE:** `/v1/swift-codes/{swift-code}`

**Response structure:**

```json
{
  "message": string
}
```
