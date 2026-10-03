# Rental Property Maintenance System (Backend Only)

A clean, production-ready backend system built using **Java 21**, **Spring Boot 3**, **Spring JDBC (JdbcTemplate)**, and **MySQL**.

---

## 🏗️ Architecture & Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.2.5
- **Data Access:** Spring JDBC (`JdbcTemplate` + `RowMapper`)
- **Database:** MySQL 8.x
- **Architecture Pattern:** Controller → Service → DAO (Data Access Object) → MySQL

```
com.property.maintenance
 ├── controller          # REST Controllers
 ├── service             # Business logic interfaces
 │    └── impl           # Service implementations
 ├── dao                 # JDBC DAO interfaces
 │    └── impl           # JdbcTemplate query implementations & RowMappers
 ├── model               # Plain Java domain entities
 ├── dto                 # Request / Response transfer objects
 ├── exception           # Custom exceptions & global handler
 └── PropertyMaintenanceApplication.java
```

---

## 📊 Database Schema

Tables created automatically from `src/main/resources/schema.sql`:

1. **`users`**: Login credentials and system roles (`ADMIN`, `PROPERTY_MANAGER`, `TECHNICIAN`, `TENANT`).
2. **`properties`**: Rental properties with unit code, address, rent, specifications, and availability status (`AVAILABLE`, `OCCUPIED`, `UNDER_MAINTENANCE`).
3. **`tenants`**: Tenant details, contact info, lease dates, and assigned property foreign key.
4. **`technicians`**: Maintenance workers with specialization (`PLUMBING`, `ELECTRICAL`, `HVAC`, `APPLIANCE`, `CARPENTRY`, `GENERAL`), hourly rates, and availability flag.
5. **`maintenance_requests`**: Maintenance tickets with priority (`LOW`, `MEDIUM`, `HIGH`, `EMERGENCY`), status workflow (`PENDING` → `ASSIGNED` → `IN_PROGRESS` → `COMPLETED` / `CANCELLED`), and scheduled/completion timestamps.
6. **`payments`**: Maintenance billing records with labor cost, parts/material cost, total cost, and payment status (`PENDING`, `PARTIAL`, `PAID`, `WAIVED`).
7. **`maintenance_records`**: Immutable historical audit trail for completed repairs.

---

## 🚀 Setup & Execution

### 1. Configure MySQL Database

Open [application.properties](file:///c:/Users/manoj/OneDrive/Attachments/projectTKT/src/main/resources/application.properties) and update your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/property_maintenance_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The app will start on port `8080` with context path `/api`. Schema and initial demo data from `schema.sql` and `data.sql` will load automatically.

---

## 📡 REST API Reference

### Phase 10: Authentication & User Accounts (`/api/auth`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Login with username/email and password |
| `POST` | `/api/auth/register` | Register a new user |
| `GET` | `/api/auth/users/{id}` | Get user details by ID |

#### Example Login Request:
```json
POST /api/auth/login
{
  "usernameOrEmail": "admin",
  "password": "admin123"
}
```

---

### Phase 2: Property Management (`/api/properties`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/properties` | Add a new property |
| `GET` | `/api/properties` | Get all properties (supports `?status=AVAILABLE&type=APARTMENT&city=Miami`) |
| `GET` | `/api/properties/{id}` | Get property by ID |
| `GET` | `/api/properties/code/{code}` | Get property by unique code (e.g. `PROP-101`) |
| `PUT` | `/api/properties/{id}` | Update property details |
| `PATCH` | `/api/properties/{id}/status?status=AVAILABLE` | Update property status |
| `DELETE` | `/api/properties/{id}` | Delete property |

#### Example Add Property Request:
```json
POST /api/properties
{
  "propertyCode": "PROP-201",
  "title": "Parkview Deluxe Condo",
  "address": "500 Park Avenue",
  "city": "Chicago",
  "state": "IL",
  "zipCode": "60601",
  "propertyType": "CONDO",
  "status": "AVAILABLE",
  "rentAmount": 2100.00,
  "bedrooms": 2,
  "bathrooms": 2,
  "description": "Renovated 2-bed unit with city skyline view"
}
```

---

### Phase 3: Tenant Management (`/api/tenants`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/tenants` | Add a new tenant |
| `GET` | `/api/tenants` | Get all tenants (supports `?status=ACTIVE&propertyId=1`) |
| `GET` | `/api/tenants/{id}` | Get tenant by ID |
| `GET` | `/api/tenants/user/{userId}` | Get tenant by linked user account ID |
| `PUT` | `/api/tenants/{id}` | Update tenant details |
| `POST` | `/api/tenants/{id}/assign-property/{propertyId}` | Assign tenant to property (sets property to `OCCUPIED`) |
| `POST` | `/api/tenants/{id}/unassign-property` | Unassign tenant (sets property back to `AVAILABLE`) |
| `DELETE` | `/api/tenants/{id}` | Delete tenant |

#### Example Add Tenant Request:
```json
POST /api/tenants
{
  "firstName": "David",
  "lastName": "Miller",
  "email": "david.miller@example.com",
  "phone": "555-0301",
  "emergencyContact": "Sarah Miller (555-0302)",
  "propertyId": 3,
  "leaseStartDate": "2026-04-01",
  "leaseEndDate": "2027-03-31",
  "status": "ACTIVE"
}
```

---

### Phase 5: Technician Management (`/api/technicians`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/technicians` | Add technician |
| `GET` | `/api/technicians` | Get all technicians (supports `?specialization=PLUMBING&isAvailable=true`) |
| `GET` | `/api/technicians/available/{specialization}` | Find active available technicians by specialization |
| `GET` | `/api/technicians/{id}` | Get technician by ID |
| `PUT` | `/api/technicians/{id}` | Update technician info/rates |
| `PATCH` | `/api/technicians/{id}/availability?isAvailable=true` | Toggle availability status |
| `DELETE` | `/api/technicians/{id}` | Delete technician |

---

### Phase 4, 6 & 7: Maintenance Request Lifecycle & Status Tracking (`/api/maintenance-requests`)

```
PENDING  ──►  ASSIGNED  ──►  IN_PROGRESS  ──►  COMPLETED
   │             │                │
   └─────────────┴────────────────┴──────────►  CANCELLED
```

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/maintenance-requests` | **Phase 4:** Create maintenance ticket (starts as `PENDING`) |
| `GET` | `/api/maintenance-requests` | View all requests (supports `?status=PENDING&priority=HIGH`) |
| `GET` | `/api/maintenance-requests/{id}` | Get request by ID |
| `GET` | `/api/maintenance-requests/ticket/{ticketNumber}` | Get request by ticket number (e.g. `TKT-20260924-1001`) |
| `GET` | `/api/maintenance-requests/tenant/{tenantId}` | Get all tickets created by a tenant |
| `GET` | `/api/maintenance-requests/technician/{techId}` | Get all tasks assigned to a technician |
| `POST` | `/api/maintenance-requests/{id}/assign` | **Phase 6:** Assign technician (`PENDING` → `ASSIGNED`) |
| `POST` | `/api/maintenance-requests/{id}/accept` | **Phase 6:** Technician accepts task (`ASSIGNED` → `IN_PROGRESS`) |
| `PATCH` | `/api/maintenance-requests/{id}/status` | **Phase 7:** Progress status or complete repair |
| `POST` | `/api/maintenance-requests/{id}/cancel` | Cancel maintenance request |

#### Step 1: Create Request
```json
POST /api/maintenance-requests
{
  "propertyId": 1,
  "tenantId": 1,
  "category": "PLUMBING",
  "priority": "HIGH",
  "description": "Kitchen sink pipe is leaking water under the cabinet"
}
```

#### Step 2: Assign Technician
```json
POST /api/maintenance-requests/1/assign
{
  "technicianId": 1,
  "scheduledDate": "2026-09-25T10:00:00",
  "notes": "Assigned to Alex Rivera (Plumber)"
}
```

#### Step 3: Technician Accepts & Starts Work
```json
POST /api/maintenance-requests/1/accept
```

#### Step 4: Complete Maintenance (Logs Cost & History Record)
```json
PATCH /api/maintenance-requests/1/status
{
  "status": "COMPLETED",
  "workPerformed": "Replaced worn rubber seal and tightened P-trap compression nut.",
  "laborCost": 120.00,
  "partsCost": 35.00,
  "notes": "Leak tested under continuous flow. No issues detected."
}
```

---

### Phase 8: Maintenance Cost & Payments (`/api/maintenance-costs`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/maintenance-costs/calculate` | Set or update labor, material/parts cost |
| `GET` | `/api/maintenance-costs/request/{requestId}` | Get billing details by request ID |
| `GET` | `/api/maintenance-costs/invoice/{invoiceNumber}` | Get invoice by invoice number |
| `GET` | `/api/maintenance-costs` | Get all invoices (supports `?status=PENDING`) |
| `POST` | `/api/maintenance-costs/{paymentId}/pay` | Process payment against an invoice |

#### Example Payment Processing:
```json
POST /api/maintenance-costs/1/pay
{
  "amount": 155.00,
  "paymentMethod": "CREDIT_CARD",
  "transactionReference": "TXN-CC-84729103"
}
```

---

### Phase 9: Maintenance History (`/api/maintenance-history`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/maintenance-history` | Query history with filters (`propertyId`, `tenantId`, `technicianId`, `startDate`, `endDate`) |
| `GET` | `/api/maintenance-history/property/{propertyId}` | Get repair history for a property |
| `GET` | `/api/maintenance-history/tenant/{tenantId}` | Get repair history for a tenant |
| `GET` | `/api/maintenance-history/technician/{techId}` | Get work logs for a technician |
| `GET` | `/api/maintenance-history/{id}` | Get specific history record |
| `GET` | `/api/maintenance-history/request/{requestId}` | Get history record by maintenance request ID |
