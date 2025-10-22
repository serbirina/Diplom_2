# Diplom_2
## Automated API Tests for "Stellar Burgers"
Tests cover scenarios of using API endpoints for user registration, login, and order creation.
Tests are implemented with Java, JUnit, REST Assured, and generate Allure reports.
---
## Tech Stack

| Technology      | Version   |
|-----------------|-----------|
| Java            | 11        |
| Maven           | 3.9.10    |
| JUnit           | 4.13.1    |
| RestAssured     | 5.2.0     |
| Gson            | 2.8.9     |
| AspectJ Weaver  | 1.9.7     |
| Maven Surefire  | 3.2.5     |
| Allure          | 2.16.0    |
---
## Run Tests
To run tests:  
`mvn clean test`
---
## Allure Report

### Open an Already Generated Report
To open an already generated report, run:  
`allure open target/allure-report`

### Generate New Report
After running all tests, a new Allure report can be generated and opened:  
`allure generate target/allure-results --clean -o target/allure-report`