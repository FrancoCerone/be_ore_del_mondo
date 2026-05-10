# Ore Del Mondo API

Backend Quarkus production-oriented for a luxury watch ecommerce website.

## Stack

- Quarkus REST JSON
- PostgreSQL
- Hibernate ORM Panache
- Flyway migrations
- JWT auth with admin role
- Bean Validation
- MapStruct DTO mapping
- OpenAPI / Swagger UI
- Health checks
- CORS
- Structured JSON logging
- Docker Compose

## Project Structure

```text
src/main/java/org/franco
  common/dto                 Shared API responses
  common/exception           Global exception handling
  config                     CORS/request logging/health support
  security/dto               Auth request/response DTOs
  security/entity            Admin user model
  security/repository        User repository
  security/resource          Auth REST API
  security/service           JWT and auth services
  watch/dto                  Watch request/response DTOs
  watch/entity               Watch and WatchImage entities
  watch/mapper               MapStruct mappers
  watch/repository           Watch repository and filters
  watch/resource             Public and admin REST APIs
  watch/service              Business logic, slug generation, pagination
src/main/resources
  application.yml
  db/migration/V1__init_ecommerce_schema.sql
```

## Run With Docker

```shell
docker compose up --build
```

The API is available at `http://localhost:8080`.

Database:

```text
Host: localhost
Port: 5432
Database: ore_del_mondo
Username: quarkus
Password: quarkus
```

Stop services:

```shell
docker compose down
```

Reset the database:

```shell
docker compose down -v
```

## Local Development

Start PostgreSQL with Docker, then run Quarkus locally:

```shell
docker compose up -d db
./mvnw quarkus:dev
```

Useful endpoints:

- Swagger UI: `http://localhost:8080/q/swagger-ui`
- OpenAPI JSON/YAML: `http://localhost:8080/q/openapi`
- Health: `http://localhost:8080/q/health`
- Readiness: `http://localhost:8080/q/health/ready`

## API Endpoints

Public:

- `GET /api/watches`
- `GET /api/watches/{slug}`
- `GET /api/watches/id/{id}`
- `GET /api/watches/brand/{brand}`
- `GET /api/watches/featured`

Auth:

- `POST /api/auth/register`
- `POST /api/auth/login`

Admin:

- `POST /api/admin/watches`
- `PUT /api/admin/watches/{id}`
- `DELETE /api/admin/watches/{id}`

Admin endpoints require:

```text
Authorization: Bearer <accessToken>
```

## Filters And Pagination

`GET /api/watches` supports:

- `brand`
- `minPrice`
- `maxPrice`
- `featured`
- `published`
- `search`
- `page`
- `size`
- `sort`

Examples:

```text
GET /api/watches?brand=Rolex&page=0&size=12&sort=price,desc
GET /api/watches?search=submariner&minPrice=5000&maxPrice=20000
GET /api/watches/featured?size=8
GET /api/watches/brand/Rolex
```

Search is case insensitive on `name`, `brand`, and `model`.

## Next.js SEO Usage

Suggested frontend routes:

- `/orologi/[slug]` -> `GET /api/watches/{slug}`
- `/brand/[brand]` -> `GET /api/watches/brand/{brand}`
- `/featured` -> `GET /api/watches/featured`

The response includes SEO fields:

- `seoTitle`
- `seoDescription`
- `slug`
- `images[].altText`
- `images[].cover`

## Request Examples

Register the first admin:

```json
{
  "email": "admin@example.com",
  "password": "ChangeMe123!"
}
```

Only the first admin can be registered from the public endpoint. After that, registration returns a conflict.

Login:

```json
{
  "email": "admin@example.com",
  "password": "ChangeMe123!"
}
```

Create a watch:

```json
{
  "name": "Rolex Submariner Date",
  "description": "Iconic luxury diver watch in excellent condition.",
  "shortDescription": "Rolex Submariner Date, full set.",
  "brand": "Rolex",
  "model": "Submariner Date",
  "price": 14900.00,
  "currency": "EUR",
  "condition": "EXCELLENT",
  "year": 2022,
  "referenceNumber": "126610LN",
  "movement": "AUTOMATIC",
  "caseMaterial": "Oystersteel",
  "strapMaterial": "Oystersteel",
  "diameter": 41.00,
  "waterResistance": "300m",
  "stock": 1,
  "featured": true,
  "published": true,
  "seoTitle": "Rolex Submariner Date 126610LN",
  "seoDescription": "Buy a Rolex Submariner Date 126610LN luxury watch.",
  "images": [
    {
      "imageUrl": "https://cdn.example.com/watches/rolex-submariner.jpg",
      "altText": "Rolex Submariner Date front view",
      "sortOrder": 0,
      "cover": true
    }
  ]
}
```

The slug is generated automatically, for example `rolex-submariner-date`.

## Response Example

```json
{
  "items": [
    {
      "id": 1,
      "uuid": "0d12d4ce-65d9-4e1d-8c4d-33c669d67683",
      "name": "Rolex Submariner Date",
      "slug": "rolex-submariner-date",
      "brand": "Rolex",
      "model": "Submariner Date",
      "price": 14900.00,
      "currency": "EUR",
      "featured": true,
      "published": true,
      "images": []
    }
  ],
  "page": 0,
  "size": 20,
  "totalItems": 1,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

## Production Notes

- Replace `JWT_SECRET` with a strong secret from your secret manager.
- Keep `quarkus.hibernate-orm.database.generation=none`; schema changes belong in Flyway migrations.
- Use `published=true` for public catalog pages and keep drafts hidden.
- Use CDN URLs for watch images; the API stores image metadata, not binary files.
- Keep page sizes bounded for frontend SEO pages.
- Run with JSON logs enabled in production.
- Restrict CORS origins to your production Next.js domains.
