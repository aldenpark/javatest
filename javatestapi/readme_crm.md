# Mini CRM Modules (Lead, Account, Contact)

This document explains how the Lead, Account, and Contact modules were added to the Spring Boot project, how to run them locally, and how to use the REST APIs.

---

## Overview

We added three JPA-backed modules with REST endpoints:

- **Lead**: early prospects with a status lifecycle (NEW, QUALIFIED, LOST, CONVERTED)
- **Account**: companies/organizations
- **Contact**: people associated with an Account

Entities use UUID primary keys, created/updated timestamps, and basic validation. Endpoints support CRUD and pagination.

---

## Spring Boot
### What Was Added (package structure)

All new code lives under `src/main/java/com/example/javatestapi/crm`:

```
src/main/java/com/example/javatestapi/
├─ crm/
│  ├─ model/            # JPA entities (@Entity) that map to DB tables.
│  │  ├─ BaseEntity.java
│  │  ├─ Lead.java
│  │  ├─ Account.java
│  │  └─ Contact.java
│  ├─ service/          # business logic
│  ├─ repository/       # Data-access layer. Interfaces that extend JpaRepository<…> so you get CRUD without writing SQL.
│  │  ├─ LeadRepository.java
│  │  ├─ AccountRepository.java
│  │  └─ ContactRepository.java
│  └─ web/       # REST controllers (@RestController) that expose HTTP endpoints and use the repositories.
│     ├─ LeadController.java
│     ├─ AccountController.java
│     └─ ContactController.java
└─ config/
   └─ CorsConfig.java     # CORS helper
```

## React
```
src/
├─ app/
│  ├─ App.jsx
│  ├─ routes.jsx
│  ├─ Layout.jsx
│  └─ ErrorBoundary.jsx
├─ lib/
│  ├─ http.js           # axios instance + interceptors
│  └─ config.js         # base URLs, env helpers
├─ modules/
│  ├─ crm/
│  │  ├─ model/         # UI-facing shapes, mappers, validators
│  │  │  └─ Lead.model.js
│  │  ├─ service/       # UI business logic (compose repository calls)
│  │  │  └─ leadsService.js
│  │  ├─ repository/    # Data-access layer: raw HTTP calls to /api/leads
│  │  │  └─ leadsApi.js
│  │  └─ web/           # React UI (pages/components)
│  │     ├─ components/
│  │     │  ├─ LeadsTable.jsx
│  │     │  └─ LeadForm.jsx
│  │     └─ pages/
│  │        └─ LeadsPage.jsx
│  └─ profiles/
│     ├─ repository/
│     │  └─ profilesApi.js
│     └─ web/
│        └─ pages/
│           └─ ProfilesPage.jsx
├─ index.jsx
└─ App.css

```

---


## Prerequisites

- Java 17
- Maven
- PostgreSQL 16+ running locally
- App DB and user:
  - Database: `javatest`
  - User: `javatest` / Password: `javatest` (or your own)
  - Permissions (run as superuser in `psql` after `\c javatest`):
    ```
    GRANT USAGE, CREATE ON SCHEMA public TO javatest;
    ```

---

## Configuration

The app reads DB settings from environment variables (with safe defaults). In `application.properties`:

spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:javatest}
spring.datasource.username=${DB_USER:javatest}
spring.datasource.password=${DB_PASSWORD:javatest}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect is optional; Hibernate auto-detects Postgres

server.port=${PORT:8080}

```
Create a `.env` (not committed) at repo root or `javatestapi/.env`:

```

DB_HOST=localhost
DB_PORT=5432
DB_NAME=javatest
DB_USER=javatest
DB_PASSWORD=javatest
PORT=8080
SPRING_PROFILES_ACTIVE=dev


---

## Build & Run

### Compile and build JAR
mvn -q clean package

### Run with Maven (dev)

from repo root (script will source .env if you use it)

./run-local.sh
or manually:

set -a && source .env && set +a
mvn -f javatestapi spring-boot:run


### Run the built JAR

mvn -f javatestapi clean package
set -a && source javatestapi/.env && set +a
java -jar javatestapi/target/javatestapi-0.0.1-SNAPSHOT.jar


---

## Database schema (logical)

Hibernate creates/updates tables automatically (ddl-auto=update). Shapes:

### leads
- id (uuid, PK)
- first_name (not null)
- last_name (not null)
- email
- phone
- company
- status (ENUM text: NEW, QUALIFIED, LOST, CONVERTED; default NEW)
- source
- active (boolean, default true)
- created_at (timestamp)
- updated_at (timestamp)

### accounts
- id (uuid, PK)
- name (unique, not null)
- industry
- website
- billing_address
- shipping_address
- active (boolean, default true)
- created_at / updated_at

### contacts
- id (uuid, PK)
- account_id (uuid, FK -> accounts.id, not null)
- first_name (not null)
- last_name (not null)
- email
- phone
- title
- active (boolean, default true)
- created_at / updated_at

---

## REST API

Base path: `http://localhost:8080/api`

### Leads

- **GET** `/api/leads`  
  Query params: `page`, `size`, `sort` (e.g., `?page=0&size=20&sort=createdAt,desc`)

- **GET** `/api/leads/{id}`

- **POST** `/api/leads`

{
"firstName": "Ava",
"lastName": "Nguyen",
"email": "ava@example.com",
"phone": "555-123-4567",
"company": "Example Co",
"source": "web",
"status": "NEW",
"active": true
}


- **PUT** `/api/leads/{id}`  
(same shape as POST; updates fields)

- **DELETE** `/api/leads/{id}`

### Accounts

- **GET** `/api/accounts`
- **GET** `/api/accounts/{id}`
- **POST** `/api/accounts`


{
"name": "Acme Inc",
"industry": "Manufacturing",
"website": "https://acme.example",
"billingAddress": "1 Main St, Ogden, UT",
"shippingAddress": "1 Main St, Ogden, UT",
"active": true
}

Notes: returns 400 if an account with the same `name` already exists.

- **PUT** `/api/accounts/{id}`
- **DELETE** `/api/accounts/{id}`

### Contacts

- **GET** `/api/contacts`
- **GET** `/api/contacts/{id}`
- **POST** `/api/contacts?accountId={ACCOUNT_ID}`



{
"firstName": "Sam",
"lastName": "Green",
"email": "sam@acme.example",
"phone": "555-888-9999",
"title": "VP",
"active": true
}


Notes: `accountId` is required (query param). The body omits `account`.

- **PUT** `/api/contacts/{id}?accountId={ACCOUNT_ID?}`  
(optional `accountId` to reassign the Contact)

- **DELETE** `/api/contacts/{id}`

---

## cURL examples

Create an Account:


curl -X POST http://localhost:8080/api/accounts
-H "Content-Type: application/json"
-d '{"name":"Acme Inc","industry":"Manufacturing","website":"https://acme.example","active":true}'


List Accounts:

curl "http://localhost:8080/api/accounts?page=0&size=10&sort=createdAt,desc"


Create a Contact for an Account:

curl -X POST "http://localhost:8080/api/contacts?accountId=ACCOUNT_ID"
-H "Content-Type: application/json"
-d '{"firstName":"Sam","lastName":"Green","email":"sam@acme.example","title":"VP"}'


Create a Lead:

curl -X POST http://localhost:8080/api/leads
-H "Content-Type: application/json"
-d '{"firstName":"Ava","lastName":"Nguyen","email":"ava@example.com","company":"Example Co","status":"NEW"}'


---

## CORS (for React dev at http://localhost:3000)

If your React app runs on port 3000, enable CORS for `/api/**`. Example `config/CorsConfig.java`:


@Configuration
public class CorsConfig implements WebMvcConfigurer {
@Override
public void addCorsMappings(CorsRegistry registry) {
registry.addMapping("/api/**")
.allowedOrigins("http://localhost:3000")
.allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
.allowCredentials(true);
}
}



---

## Seeding options (optional)

- **Quick SQL**: run INSERTs in DBeaver connected to `javatest`.
- **`data.sql`**: set `spring.sql.init.mode=always` and add `src/main/resources/data.sql`.
- **Flyway**: add `flyway-core` and create `db/migration/V1__crm.sql` for schema and `V2__seed.sql` for sample data.

Example `V1__crm.sql` (if using Flyway):


CREATE TABLE IF NOT EXISTS accounts (...);
CREATE TABLE IF NOT EXISTS leads (...);
CREATE TABLE IF NOT EXISTS contacts (...);
ALTER TABLE contacts ADD CONSTRAINT fk_contacts_account FOREIGN KEY (account_id) REFERENCES accounts(id);


---

## Troubleshooting

- **Permission denied for schema public**  
  Connect as superuser, then:


\c javatest
GRANT USAGE, CREATE ON SCHEMA public TO javatest;



- **App can’t connect**  
Ensure `.env` is loaded and DB is reachable:
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- **DBeaver doesn’t show `javatest`**  
Edit Connection → PostgreSQL tab → check “Show all databases”, then reconnect.

---

## Future Enhancements

- Lead conversion: Lead → Account + primary Contact
- Basic search (by name/email/company)
- Soft-delete (`deletedAt`) or archiving
- Validation groups and custom constraints
- Request/response DTOs + MapStruct for clean API contracts
- Flyway migrations for production safety

---

