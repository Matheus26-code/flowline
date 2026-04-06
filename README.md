# FlowLine 🏭

> **Real-time material flow visibility for factories and warehouses**

FlowLine is a B2B SaaS back-end platform that enables industrial operations teams to track, manage, and monitor internal material movement across sectors — bringing transparency to the factory floor.

Built from a real operational background in automotive manufacturing (GKN Automotive), FlowLine solves a genuine problem: the lack of visibility into who moved what, where, and when.

---

## 🚀 Live Demo

**API Base URL:** `https://flowline-production.up.railway.app`

**Swagger Documentation:** `https://flowline-production.up.railway.app/swagger-ui/index.html`

> All endpoints except `/api/auth/login` require a Bearer Token. Use the login endpoint to obtain one.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA + Hibernate |
| Migrations | Flyway |
| Containerization | Docker + Docker Compose |
| Build Tool | Gradle |
| Documentation | SpringDoc OpenAPI (Swagger) |
| Deployment | Railway |
| Utilities | Lombok |

---

## 🏗️ Architecture

This project follows a **layered architecture** with clear separation of concerns:

```
com.flowline.flowline
├── config          # Spring Security configuration
├── controller      # HTTP layer — receives requests, delegates to service
├── dto             # Data Transfer Objects — API contracts (request/response)
├── exception       # Custom exceptions — domain-specific errors
├── handler         # Global exception handler — consistent error responses
├── model           # Domain entities — mirrors database tables
├── repository      # Data access layer — communicates with the database
├── security        # JWT filter, token service, UserDetails implementation
└── service         # Business logic layer — rules and orchestration
```

**Key architectural decisions:**

- **Constructor injection** over field injection — enforces immutability and improves testability
- **DTO pattern** — decouples API contracts from database entities, preventing over-exposure
- **Records for DTOs** — immutable by design, no boilerplate needed
- **Flyway migrations** — versioned schema management, safe for production environments
- **Spring Profiles** — separate configurations for `dev` and `prod` environments
- **JWT stateless authentication** — no server-side session storage, horizontally scalable

---

## 📊 Domain Model

```
Warehouse (root entity — represents a company/factory)
  └── Sector       (physical area within the warehouse)
  └── Product      (material catalog, per warehouse)
  └── User         (operator, manager, or admin)

MovementOrder      (tracks material movement between sectors)
  └── originSector
  └── destinationSector
  └── product
  └── createdBy (User)
  └── status: PENDING | DELIVERING | DELIVERED | CANCELLED
```

---

## 📋 API Endpoints

### Authentication
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/auth/login` | Authenticate and receive JWT token | Public |

### Warehouse
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/warehouse` | Create a warehouse |
| `GET` | `/api/warehouse` | List all warehouses (paginated) |
| `GET` | `/api/warehouse/{id}` | Find warehouse by ID |
| `PUT` | `/api/warehouse/{id}` | Update warehouse |
| `DELETE` | `/api/warehouse/{id}` | Delete warehouse |

### User
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/user` | Create a user |
| `GET` | `/api/user` | List all users (paginated) |
| `GET` | `/api/user/{id}` | Find user by ID |
| `PUT` | `/api/user/{id}` | Update user |
| `DELETE` | `/api/user/{id}` | Delete user |

### Sector
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/sector` | Create a sector |
| `GET` | `/api/sector` | List all sectors (paginated) |
| `GET` | `/api/sector/{id}` | Find sector by ID |
| `PUT` | `/api/sector/{id}` | Update sector |
| `DELETE` | `/api/sector/{id}` | Delete sector |

### Product
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/product` | Create a product |
| `GET` | `/api/product` | List all products (paginated) |
| `GET` | `/api/product/{id}` | Find product by ID |
| `PUT` | `/api/product/{id}` | Update product |
| `DELETE` | `/api/product/{id}` | Delete product |

### Movement Orders
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/orders` | Create a movement order |
| `GET` | `/api/orders` | List all orders (paginated) |
| `GET` | `/api/orders/{id}` | Find order by ID |
| `PUT` | `/api/orders/{id}` | Update order |
| `DELETE` | `/api/orders/{id}` | Delete order |

---

## 🔐 Authentication Flow

```
1. POST /api/auth/login → { email, password }
2. Server returns → { token: "eyJhbGci..." }
3. Include in all requests → Authorization: Bearer <token>
4. Token expires after 24 hours
```

### User Roles
| Role | Description |
|---|---|
| `ADMIN` | Full system access |
| `MANAGER` | Can update/cancel any movement order |
| `OPERATOR` | Creates and executes movement orders |
| `ASSISTANT` | Read-only access |

---

## 🚀 Running Locally

### Prerequisites

- [Java 21+](https://adoptium.net/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Gradle](https://gradle.org/) (or use the included `./gradlew` wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/Matheus26-code/flowline.git
cd flowline
```

### 2. Create your local environment files

Create `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/flowline
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Create `.env` in the project root:

```
POSTGRES_PASSWORD=your_password
```

> ⚠️ Both files are listed in `.gitignore` and will never be committed.

### 3. Start the database

```bash
docker compose up -d
```

### 4. Run the application

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 🧪 Database Schema

Managed by Flyway. Migrations located at `src/main/resources/db/migration/`.

| Version | Description |
|---|---|
| V1 | Create warehouse table |
| V2 | Create users table |
| V3 | Create sector table |
| V4 | Create product table |
| V5 | Create movement order table |

---

## 🌍 Environment Profiles

| Profile | Usage |
|---|---|
| `dev` | Local development with direct credentials |
| `prod` | Production using environment variables |

### Production environment variables

| Variable | Description |
|---|---|
| `DB_URL` | Full JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for signing JWT tokens (min 256 bits) |
| `JWT_EXPIRATION` | Token expiration in milliseconds (e.g. `86400000` = 24h) |

---

## 🧪 Testing

This project includes unit tests for the service layer using **JUnit 5** and **Mockito**.

### Running the tests
```bash
./gradlew test
```

### Test coverage

| Service | Tests |
|---|---|
| WarehouseService | 6 tests |
| ProductService | 6 tests |
| SectorService | 6 tests |
| UserService | in progress |
| MovementOrderService | in progress |

**Patterns applied:**
- `@ExtendWith(MockitoExtension.class)` for isolated unit tests
- `@Mock` for repository dependencies
- `@InjectMocks` for the service under test
- `@BeforeEach` for shared test fixtures
- `assertThrows` for exception scenario coverage
- `verify` for void method behavior validation


## 👨‍💻 Author

**Igor Matheus Braz Carvalho** — Back-end Developer

- GitHub: [@Matheus26-code](https://github.com/Matheus26-code)
- LinkedIn: [linkedin.com/in/igor-matheus-braz](https://linkedin.com/in/igor-matheus-braz)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).