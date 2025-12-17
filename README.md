# LogiFlow — Microservicios de entrega (U2)

Este repositorio contiene un sistema basado en microservicios para una app de entregas. Incluye servicios de autenticación, gestión de flota, pedidos y facturación (Billing). Está construido principalmente con Spring Boot (Java 17), Maven y PostgreSQL, y soporta ejecución local con Docker.

## 🧭 Contexto del proyecto

- Arquitectura por microservicios (cada servicio con su propio `pom.xml`):
	- `auth-service`: autenticación y gestión de usuarios/roles.
	- `fleet-service`: registro y estado de vehículos (camión/livianos), repositorio de flota.
	- `pedido_service`: base del servicio de pedidos (pendiente de expansión).
	- `billing-service`: cálculo de tarifa básica y generación de factura en estado BORRADOR, persistida en PostgreSQL.

- Base de datos: PostgreSQL.
	- Puede apuntar al servidor principal del usuario en `localhost:5432`.
	- Alternativa de desarrollo: contenedor de Postgres y pgAdmin vía `docker-compose` dentro de `billing-service` (puerto `5433` para evitar conflictos).

- ORM: Spring Data JPA (Hibernate 7). El esquema se crea/actualiza con `ddl-auto=update`.

## 📦 Tecnologías y versiones

- Java 17
- Spring Boot 4.x
- Maven
- Spring MVC, Jakarta Validation
- Spring Data JPA + Hibernate
- PostgreSQL + HikariCP
- Docker y Docker Compose (opcional para desarrollo)
- pgAdmin (opcional para inspección de DB)

## 🗂️ Estructura principal

```
distribuidas_pryU2_logiflow/
	README.md
	auth-service/
	fleet-service/
	pedido_service/
	billing-service/
		docker-compose.yml   # Postgres + pgAdmin (dev)
		src/main/resources/application.yaml
		src/main/java/... BillingServiceApplication.java, controller, service, model, repository
```

## 🧩 BillingService — Funcionalidad mínima

Endpoints (puerto por defecto: `8080`):

- POST `/api/billing/calculate`
	- Request JSON: `{ "distanceKm": number, "durationMin": number }`
	- Respuesta: `BigDecimal` con el valor calculado.
	- Fórmula: `BASE_FARE(2.50) + PER_KM(1.20)*distanceKm + PER_MIN(0.25)*durationMin`.

- POST `/api/billing/invoices?customerId=...`
	- Request JSON: igual que `/calculate`.
	- Crea una factura en estado `BORRADOR` y la persiste en la tabla `invoices`.
	- Respuesta: `InvoiceResponseDto` con `{ id, customerId, amount, state, createdAt }`.

Entidad JPA `Invoice`:
- `id` (UUID String), `customer_id`, `amount`, `state` (ENUM almacenado como STRING), `created_at`.

## 🚀 Cómo ejecutar

### Prerrequisitos

- Java 17 y Maven instalados.
- Opcional: Docker y Docker Compose para levantar Postgres/pgAdmin de desarrollo.

### Opción A: Usar tu PostgreSQL en `localhost:5432`

1) Asegúrate de tener una base `delivery` y un usuario con permisos.
	 - Por defecto el servicio usa: usuario `delivery_user`, contraseña `qwerty123` (puedes cambiarlo vía variables de entorno o editar `application.yaml`).

2) Compila y ejecuta `billing-service`:

```zsh
cd billing-service
mvn -DskipTests package
mvn -DskipTests spring-boot:run
```

3) Prueba los endpoints (ejemplos):

```zsh
# Calcular tarifa
curl -i -X POST http://localhost:8080/api/billing/calculate \
	-H "Content-Type: application/json" \
	-d '{"distanceKm":4.5,"durationMin":12}'

# Crear factura BORRADOR para customerId=cliente123
curl -i -X POST 'http://localhost:8080/api/billing/invoices?customerId=cliente123' \
	-H "Content-Type: application/json" \
	-d '{"distanceKm":4.5,"durationMin":12}'
```

### Opción B: Levantar Postgres + pgAdmin con Docker (puerto 5433)

Esta opción evita conflictos si ya tienes Postgres en 5432.

```zsh
cd billing-service
docker compose up -d

# Compilar y ejecutar la app
mvn -DskipTests package
mvn -DskipTests spring-boot:run
```

pgAdmin estará disponible en `http://localhost:8085` (usuario y contraseña por defecto definidos en `docker-compose.yml`).

### Variables de entorno útiles

Puedes sobrescribir la conexión sin tocar `application.yaml` usando variables estándar de Spring:

```zsh
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/delivery'
export SPRING_DATASOURCE_USERNAME='delivery_user'
export SPRING_DATASOURCE_PASSWORD='qwerty123'

# Ejecutar
mvn -DskipTests spring-boot:run
```

Si usas el contenedor en 5433:

```zsh
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5433/delivery'
export SPRING_DATASOURCE_USERNAME='delivery_user'
export SPRING_DATASOURCE_PASSWORD='qwerty123'
mvn -DskipTests spring-boot:run
```

## 🔍 Verificación en la base de datos

Con pgAdmin (Docker):
- Conéctate al servidor `localhost:5433` (o `5432` si usas tu instancia).
- Base de datos: `delivery`.
- Esquema `public`, tabla `invoices`: verifica filas insertadas tras crear facturas.

Con `psql` en Docker:

```zsh
docker exec -it billing-service-postgres psql -U delivery_user -d delivery -c "SELECT id, customer_id, amount, state, created_at FROM invoices ORDER BY created_at DESC LIMIT 5;"
```

## 🧪 Pruebas (pendiente/opcional)

- Añadir tests unitarios para el cálculo de tarifa y tests de integración con Testcontainers/H2.
- Ejecutar: `mvn test`.

## 🧱 Notas de arquitectura

- Cada servicio es independiente, con su propio `pom.xml`.
- `billing-service` usa JPA y crea la tabla `invoices` automáticamente (`ddl-auto=update`).
- El estado de factura se almacena como texto (`BORRADOR`, `EMITIDA`, `PAGADA`, `CANCELADA`).
- No se realizaron cambios en un API Gateway dentro de este repositorio durante esta implementación.

## 🛠️ Solución de problemas

- Error de autenticación Postgres (ej.: `FATAL: password authentication failed for user "delivery_user"`):
	- Verifica usuario/contraseña y permisos en la base `delivery`.
	- Alternativamente, exporta credenciales válidas vía variables de entorno (ver sección anterior).
	- Asegura que el usuario tenga `CONNECT` a la base y privilegios `USAGE/CREATE` en el esquema `public`.

- Error de dialecto Hibernate (por fallo de metadata JDBC):
	- Suele ser consecuencia de no poder conectarse a la DB. Soluciona la autenticación.

- Puerto ocupado (5432):
	- Usa la opción Docker en 5433 para desarrollo, o detén la instancia que ocupa 5432.

## 📄 Licencia

Proyecto académico. Uso interno para la materia de Sistemas Distribuidos.