# URL Safety Checker

A Safe Browsing–style microservice built with Java 17 and Spring Boot that analyzes URLs and classifies them as Safe, Suspicious, or Malicious using heuristic-based risk scoring.

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
- Database-backed blacklist
- URL and domain blacklist matching
- Admin API for blacklist management
- Redirect resolution using Java HTTP Client
- Automatic redirect following
- Final URL and domain blacklist checks
- Analysis of both original and redirected URLs
- Detection of redirect resolution failures

## API

### Scan URL

**Endpoint**

POST `/api/v1/scan`

**Request**

    {
      "url": "https://example.com"
    }

**Response**

    {
      "url": "https://example.com",
      "classification": "SAFE",
      "riskScore": 0,
      "signals": []
    }

### Example

**Request**

    {
      "url": "http://192.168.1.10/login"
    }

**Response**

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
| Unable to resolve URL | +20 |
| URL/domain found in blacklist | +100 |

## Blacklist

The application maintains a database-backed blacklist with two entry types:

- `URL` — matches an exact URL
- `DOMAIN` — matches a domain and therefore applies to URLs under that domain

Blacklist entries are stored in MySQL using Spring Data JPA.

### Add Blacklist Entry

**Endpoint**

POST `/api/v1/admin/blacklist`

**Example**

    POST /api/v1/admin/blacklist?value=evil.com&type=DOMAIN

Supported types:

- `DOMAIN`
- `URL`

### Blacklist Detection

The scanner checks:

1. The original URL
2. The original domain
3. If a redirect occurs:
  - The final URL
  - The final domain

A blacklist match produces a high-confidence risk signal and classifies the URL as `MALICIOUS`.

## Redirect Analysis

The application uses Java's `java.net.http.HttpClient` to resolve HTTP redirects.

The HTTP client:

- Automatically follows normal redirects
- Uses connection timeouts
- Uses request-level timeouts
- Initially attempts a `HEAD` request
- Falls back to `GET` when `HEAD` is not allowed
- Returns the final URI after redirect resolution

### URL Scanning Flow

    Original URL
       ↓
    Validate
       ↓
    Blacklist original URL/domain
       ↓
    Resolve redirect
       ↓
    If redirect succeeded + destination differs
       ├── Check final URL blacklist
       └── Check final domain blacklist
       ↓
    Analyze original URL
       ↓
    If redirected → analyze final URL too
       ↓
    If resolution failed → add "Unable to resolve URL" +20
       ↓
    Calculate score
       ↓
    Classification

The scanner first validates the submitted URL and checks it against the URL and domain blacklists. If the URL resolves successfully to a different destination, the final URL and domain are also checked against the blacklist and analyzed for additional risk signals. If redirect resolution fails, the original URL is still analyzed and an `Unable to resolve URL` signal is added with a risk score of 20, ensuring the result is classified as at least `SUSPICIOUS`.

## Architecture

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
      +----> BlackListService
      |           |
      |           v
      |     BlackListEntryRepository
      |           |
      |           v
      |         MySQL
      |
      +----> UrlRedirectClient
      |           |
      |           v
      |      Java HttpClient
      |
      +----> RiskAnalyzer
                  |
                  +----> Risk Signals
      |
      v
    ScanResponse

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Java HTTP Client (`java.net.http`)
- Maven

## Project Structure

    src
    └── main
        └── java
            └── com.chandana.urlsafetychecker
                ├── controller
                │   ├── ScanController
                │   └── BlackListController
                │
                ├── dto
                │   ├── ScanRequest
                │   └── ScanResponse
                │
                ├── model
                │   ├── BlackListEntry
                │   ├── BlackListType
                │   ├── Classification
                │   ├── RedirectResult
                │   └── RiskSignal
                │
                ├── repository
                │   └── BlackListEntryRepository
                │
                ├── service
                │   ├── ScanService
                │   └── BlackListService
                │
                ├── analyzer
                │   └── RiskAnalyzer
                │
                ├── client
                │   └── UrlRedirectClient
                │
                └── util
                    └── UrlUtils

## Local Setup

### Prerequisites

- Java 17
- Maven
- MySQL

### MySQL Setup

Create the database and user using MySQL Workbench or the MySQL CLI:

    CREATE DATABASE url_safety
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

    CREATE USER 'urlsafe'@'localhost' IDENTIFIED BY 'pass123';

    GRANT ALL PRIVILEGES ON url_safety.* TO 'urlsafe'@'localhost';

    FLUSH PRIVILEGES;

### Application Configuration

Update the database configuration in:

`src/main/resources/application.properties`

Example:

    spring.application.name=url-safety-checker

    spring.datasource.url=jdbc:mysql://localhost:3306/url_safety?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    spring.datasource.username=urlsafe
    spring.datasource.password=pass123
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.open-in-view=false

    spring.jpa.properties.hibernate.connection.charSet=UTF-8
    spring.jpa.properties.hibernate.connection.characterEncoding=utf8mb4
    spring.jpa.properties.hibernate.connection.useUnicode=true

    spring.datasource.hikari.maximum-pool-size=10
    spring.datasource.hikari.minimum-idle=2
    spring.datasource.hikari.connection-timeout=30000

> For production environments, credentials should be supplied through environment variables or a secrets manager rather than committed to source control.

### Run the Application

Using the Maven wrapper:

    ./mvnw spring-boot:run

The application runs on:

`http://localhost:8080`

### Run Tests

    ./mvnw clean test

## Example Requests

### Safe URL

    curl -X POST http://localhost:8080/api/v1/scan \
      -H "Content-Type: application/json" \
      -d '{"url":"https://example.com"}'

### Suspicious URL

    curl -X POST http://localhost:8080/api/v1/scan \
      -H "Content-Type: application/json" \
      -d '{"url":"http://192.168.1.10/login"}'

### Add Domain to Blacklist

    curl -X POST \
      "http://localhost:8080/api/v1/admin/blacklist?value=evil.com&type=DOMAIN"

### Add URL to Blacklist

    curl -X POST \
      "http://localhost:8080/api/v1/admin/blacklist?value=https://evil.com/login&type=URL"

## Planned Features

- SSRF protection
- Scan history and audit logs
- Redis caching
- Unit and integration test coverage
- Swagger/OpenAPI documentation
- Docker support
- Production-ready configuration

## Future Improvements

The risk engine is intentionally heuristic-based. Future versions can improve detection accuracy by introducing:

- More sophisticated URL normalization
- Additional malicious URL patterns
- Domain reputation checks
- DNS-based analysis
- Improved redirect analysis
- Rate limiting
- Distributed caching
- Persistent scan history
- Authentication and authorization for admin APIs

## Disclaimer

This project is an educational and engineering exercise inspired by Safe Browsing–style URL analysis. The heuristic scoring system does not guarantee that a URL is safe or malicious.

The project should not be considered a replacement for commercial URL reputation or threat intelligence services.

## License

This project is intended for educational and portfolio purposes.