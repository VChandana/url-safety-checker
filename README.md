# url-safety-checker

# URL Safety Checker

A Safe Browsing–style microservice built with **Java 17** and **Spring Boot** that classifies URLs as **Safe**, **Suspicious**, or **Malicious** using:

- Blacklist checks (domain, URL, regex)
- Redirect crawling & final URL analysis
- Heuristic scoring rules
- In-memory caching for repeat lookups
- Admin APIs for managing blacklist entries and audit logs

## Tech stack
- Java 17 · Spring Boot · Spring Data JPA · MySQL (dev)
- HTTP Client (java.net.http)
- Maven

## Local setup (MySQL)
1. Create DB + user (MySQL Workbench or CLI)
```sql
CREATE DATABASE url_safety CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'urlsafe'@'localhost' IDENTIFIED BY 'pass123';
GRANT ALL PRIVILEGES ON url_safety.* TO 'urlsafe'@'localhost';
FLUSH PRIVILEGES;
