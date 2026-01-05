# restassured-mysql-simple-demo

A minimal API automation testing project using Rest Assured with MySQL database verification, built in Java using Maven and JUnit 5.

This project demonstrates how to validate REST API responses and verify data persistence at the database level using a real public practice API.

---

## Project Overview

This repository showcases a simple real-world API automation pattern where:

- A REST API is tested using Rest Assured
- API response data is stored in a MySQL database
- Database records are validated using JDBC
- API GET responses are compared against database values for consistency

The project uses the public practice API **Restful-Booker**.

---

## Tech Stack

- Java  
- Rest Assured  
- JUnit 5  
- MySQL  
- JDBC  
- Maven  
- IntelliJ IDEA  

---

## Practice API Used
Base URL: https://restful-booker.herokuapp.com

API Documentation:https://restful-booker.herokuapp.com/apidoc/index.html


---

## Project Structure


restassured-mysql-simple-demo
├─ pom.xml
└─ src
└─ test
└─ java
└─ com.demo
├─ BaseTest.java
├─ DbUtil.java
└─ BookingApiDbTest.java


---

## Test Flow

1. Send POST request to create a booking
2. Extract booking details from API response
3. Store booking data in MySQL database
4. Verify booking record exists in database
5. Send GET request for the same booking
6. Compare API response values with database values

---

## Database Setup

Run the following SQL in MySQL:

```sql```
CREATE DATABASE IF NOT EXISTS restassured_demo;
USE restassured_demo;

CREATE TABLE IF NOT EXISTS booking_audit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  booking_id INT NOT NULL,
  firstname VARCHAR(50),
  lastname VARCHAR(50),
  totalprice INT,
  depositpaid BOOLEAN,
  checkin DATE,
  checkout DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_booking_id (booking_id)
);

Configuration

Update MySQL credentials in DbUtil.java:

private static final String JDBC_URL = "jdbc:mysql://localhost:3306/restassured_demo";
private static final String JDBC_USER = "root";
private static final String JDBC_PASS = "your_password";



Base URL:

