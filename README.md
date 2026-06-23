# Inventory Allocation

Spring Boot service that allocates limited component inventory across group items (kits) using demand, priority, and proportional splitting when stock is short.

## Prerequisites

- **Java 21** (Gradle toolchain will use it automatically if installed)
- Internet access for the first build (Gradle downloads dependencies once)

## Run the application

```bash
git clone https://github.com/Hardik-Rao/inventoryAllocation.git
cd inventoryAllocation
./gradlew bootRun
```

On Windows:

```bash
gradlew.bat bootRun
```

The app starts on **http://localhost:8091**.

First run may take a few minutes while Gradle and dependencies download.

## Sample data

On startup, `src/main/resources/data.sql` loads sample inventory and group-item recipes into an in-memory **H2** database. Use date **`2026-06-23`** when calling the allocation API.

## API endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/allocation/run?date=2026-06-23` | Run allocation for the given date |
| `GET` | `/api/allocation/inventory-totals` | Total available quantity per component |

### Examples

```bash
# Run allocation
curl -X POST "http://localhost:8091/api/allocation/run?date=2026-06-23"

# View inventory totals
curl "http://localhost:8091/api/allocation/inventory-totals"
```

## H2 console (optional)

While the app is running, open **http://localhost:8091/h2-console** with:

| Setting | Value |
|---------|-------|
| JDBC URL | `jdbc:h2:mem:inventorydb` |
| Username | `sa` |
| Password | *(leave empty)* |

## Run tests

```bash
./gradlew test
```

## Tech stack

- Java 21
- Spring Boot 4.1
- Spring Data JPA
- H2 (in-memory)
