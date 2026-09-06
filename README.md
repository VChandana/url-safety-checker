# URL Safety Checker

A Safe Browsing–style microservice built with **Java 17** and **Spring Boot** that analyzes URLs and classifies them as **Safe**, **Suspicious**, or **Malicious** using heuristic-based risk scoring.

## Features

- REST API for URL safety scanning
- Request validation for submitted URLs
- HTTP/HTTPS URL validation
- URI parsing using Java's `java.net.URI`
- Heuristic-based risk scoring
- Risk classification:
    - SAFE
    - SUSPICIOUS
    - MALICIOUS
- Explainable risk signals
- Detection of:
    - HTTP connections
    - IP addresses used as hosts
    - IP-like patterns in hostnames
    - Suspicious keywords
    - Suspicious TLDs
    - Unusually long URLs

## API

### Scan URL

**Endpoint**

`POST /api/v1/scan`

**Request**

```json
{
  "url": "https://example.com"
}
```

**Response**

```json
{
  "url": "https://example.com",
  "classification": "SAFE",
  "riskScore": 0,
  "signals": []
}
```

### Example

Request:

```json
{
  "url": "http://192.168.1.10/login"
}
```

Response:

```json
{
  "url": "http://192.168.1.10/login",
  "classification": "SUSPICIOUS",
  "riskScore": 45,
  "signals": [
    {
      "name": "HTTP connection",
      "score": 10
    },
    {
      "name": "IP address used as host",
      "score": 20
    },
    {
      "name": "Suspicious keyword detected",
      "score": 15
    }
  ]
}
```

## Risk Classification

The current heuristic score is mapped to a classification using the following thresholds:

| Risk Score | Classification |
|------------|----------------|
| 0–19       | SAFE           |
| 20–49      | SUSPICIOUS     |
| 50+        | MALICIOUS      |

A URL can accumulate multiple risk signals. Individual signals do not automatically mean that a URL is malicious.

## Current Risk Signals

| Signal | Score |
|--------|------:|
| HTTP connection | +10 |
| IP address used as host | +20 |
| IP-like pattern in hostname | +20 |
| Suspicious keyword detected | +15 |
| Suspicious TLD | +15 |
| Unusually long URL | +10 |

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Java HTTP Client (`java.net.http`)
- Maven

## Architecture

```text
Client
  |
  | POST /api/v1/scan
  v
ScanController
  |
  v
ScanService
  |
  +----> UrlUtils
  |
  +----> RiskAnalyzer
              |
              +----> Risk Signals
  |
  v
ScanResponse
```

## Local Setup

### Prerequisites

- Java 17
- Maven
- MySQL

### MySQL Setup

Create the database and user using MySQL Workbench or the MySQL CLI:

```sql
CREATE DATABASE url_safety
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE USER 'urlsafe'@'localhost' IDENTIFIED BY 'pass123';

GRANT ALL PRIVILEGES ON url_safety.* TO 'urlsafe'@'localhost';

FLUSH PRIVILEGES;
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The application runs on:

`http://localhost:8080`

### Run Tests

```bash
./mvnw clean test
```

## Planned Features

- Database-backed blacklist checks
- Domain and URL blacklist matching
- Redirect crawling and final URL analysis
- SSRF protection
- Scan history and audit logs
- Redis caching
- Admin APIs for blacklist management
- Unit and integration test coverage
- Swagger/OpenAPI documentation
- Docker support
- Production-ready configuration
