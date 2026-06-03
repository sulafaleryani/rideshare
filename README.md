# 🚗 RideShare Backend API

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-orange.svg)](https://jwt.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A **production-quality** ride-sharing backend API inspired by Uber/Lyft, built with Spring Boot 3 and Java 21. This project demonstrates a complete ride-hailing platform with user authentication, ride management, driver tracking, fare calculation, and payment processing.

## 🎯 Features

### ✅ Implemented
- **User Authentication & Authorization**
  - JWT-based stateless authentication
  - Role-based access control (Rider, Driver, Admin)
  - Secure password encryption (BCrypt)
  - User registration and login

- **Ride Management**
  - Request a ride with pickup/destination locations
  - Accept, start, complete, and cancel rides
  - Real-time ride status tracking
  - Ride history for riders and drivers

- **Driver Features**
  - Update availability status
  - Share current location (latitude/longitude)
  - View assigned rides
  - Driver profile management

- **Fare Calculation**
  - Distance-based fare estimation using Haversine formula
  - Dynamic pricing (base fare + per km rate)
  - Fare breakdown in response

- **Payment Processing**
  - Multiple payment methods (Cash, Credit Card, Digital Wallet)
  - Payment status tracking
  - Transaction reference generation
  - Payment history

- **Admin Features**
  - User management (suspend/activate)
  - Driver management
  - View all rides and users
  - Role-based admin endpoints

- **API Documentation**
  - Swagger/OpenAPI 3.0 integration
  - Interactive API testing UI
  - Request/response schemas

### 🚧 Planned Enhancements
- WebSocket real-time driver tracking
- Redis caching for active rides
- Rating system for riders and drivers
- Promo codes and discounts
- Advanced driver-rider matching algorithm
- Mobile app push notifications

## 🏗️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Core language |
| Spring Boot | 3.2.5 | Application framework |
| Spring Security | 3.2.5 | Authentication & Authorization |
| Spring Data JPA | 3.2.5 | Database ORM |
| PostgreSQL | 15 | Production database |
| H2 Database | (test) | Testing database |
| JWT (JJWT) | 0.12.5 | Token generation/validation |
| Lombok | 1.18.32 | Boilerplate reduction |
| Maven | 3.8+ | Build tool |
| Swagger/OpenAPI | 2.5.0 | API documentation |
| Hibernate | 6.3+ | JPA implementation |

## 📁 Project Structure
