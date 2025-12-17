# LogiFlow — Sistema de microservicios

Este repositorio contiene un sistema basado en microservicios para una aplicación de entregas. Incluye servicios de autenticación, gestión de flota, pedidos y facturación. Está construido con Spring Boot (Java 17), Maven y PostgreSQL, y soporta ejecución local con Docker.

## Índice

- [Contexto del proyecto](#contexto-del-proyecto)
- [Tecnologías](#tecnologías)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Configuración por servicio](#configuración-por-servicio)
- [Ejecución de los servicios](#ejecución-de-los-servicios)
- [Endpoints por servicio](#endpoints-por-servicio)
- [API Gateway (Kong)](#api-gateway-kong)
	- [Arranque](#arranque)
	- [Rutas configuradas](#rutas-configuradas)
	- [Pruebas por el proxy](#pruebas-por-el-proxy)
	- [Cómo ver el API Gateway](#cómo-ver-el-api-gateway)
	- [Consideraciones](#consideraciones)
- [Verificación en la base de datos](#verificación-en-la-base-de-datos)
- [Pruebas](#pruebas)
- [Consideraciones de arquitectura](#consideraciones-de-arquitectura)
- [Solución de problemas](#solución-de-problemas)
- [Licencia](#licencia)

## Contexto del proyecto

Arquitectura por microservicios (cada servicio con su propio `pom.xml`):
- `auth-service`: autenticación y gestión de usuarios y roles.
- `fleet-service`: registro y estado de vehículos (motorizados, camiones y livianos).
- `pedido_service`: base del servicio de pedidos (estructura inicial, pendiente de definición de endpoints).
- `billing-service`: cálculo de tarifa básica y generación de factura en estado BORRADOR, con persistencia en PostgreSQL.

Base de datos: PostgreSQL. Se puede apuntar al servidor principal del usuario en `localhost:5432`. Para desarrollo, existe una alternativa mediante contenedor de Postgres y pgAdmin vía `docker-compose` dentro de `billing-service` (puerto `5433`) para evitar conflictos.

ORM: Spring Data JPA (Hibernate). El esquema se crea o actualiza con `ddl-auto=update` en cada servicio.

## Tecnologías

- Java 17
- Spring Boot 4.x
- Maven
- Spring MVC, Jakarta Validation
- Spring Data JPA + Hibernate
- PostgreSQL + HikariCP
- Docker y Docker Compose (opcional)
- pgAdmin (opcional)

## Estructura del repositorio

```
distribuidas_pryU2_logiflow/
	README.md
	auth-service/
	fleet-service/
	pedido_service/
	billing-service/
		docker-compose.yml
		src/main/resources/application.yaml
		src/main/java/... BillingServiceApplication.java, controller, service, model, repository
```

## Configuración por servicio

- `auth-service` (puerto 8081)
	- Base de datos: `jdbc:postgresql://localhost:5432/logiflow_auth`
	- Usuario: `postgres`
	- Contraseña: `root` (actualiza según tu entorno)

- `fleet-service` (puerto 8083)
	- Base de datos: `jdbc:postgresql://localhost:5432/logiflow_fleet`
	- Usuario: `postgres`
	- Contraseña: `root` (actualiza según tu entorno)

- `pedido_service` (puerto 8080)
	- Base de datos: `jdbc:postgresql://localhost:5432/pedido_db`
	- Usuario: `postgres`
	- Contraseña: `slvplanA2003` (actualiza según tu entorno)

- `billing-service` (puerto 8080)
	- Base de datos: `jdbc:postgresql://localhost:5432/delivery`
	- Usuario: `delivery_user`
	- Contraseña: `qwerty123`

Cada servicio define su configuración en `src/main/resources/application.yaml`. Se pueden sobrescribir mediante variables de entorno estándar de Spring (ver más abajo).

## Ejecución de los servicios

Prerrequisitos: Java 17 y Maven instalados. Docker y Docker Compose son opcionales (solo necesarios si se usa la alternativa de base de datos en contenedor).

### Ejecución con PostgreSQL local (puerto 5432)

Para cada servicio:

```zsh
cd <nombre-del-servicio>
mvn -DskipTests package
mvn -DskipTests spring-boot:run
```

Variables de entorno de ejemplo para sobrescribir conexión:

```zsh
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/<base>'
export SPRING_DATASOURCE_USERNAME='<usuario>'
export SPRING_DATASOURCE_PASSWORD='<contraseña>'
mvn -DskipTests spring-boot:run
```

### Ejecución con Docker (solo `billing-service`)

Dentro de `billing-service` existe `docker-compose.yml` que levanta Postgres en `5433` y pgAdmin en `8085`.

```zsh
cd billing-service
docker compose up -d
mvn -DskipTests package
mvn -DskipTests spring-boot:run
```

Si se usa el contenedor en `5433`, se puede ajustar la conexión con variables de entorno:

```zsh
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5433/delivery'
export SPRING_DATASOURCE_USERNAME='delivery_user'
export SPRING_DATASOURCE_PASSWORD='qwerty123'
mvn -DskipTests spring-boot:run
```

## Endpoints por servicio

### auth-service (puerto 8081)

- `POST /api/auth/register` — Registrar usuario.
- `POST /api/auth/login` — Autenticar usuario.
- `GET /api/auth` — Listar usuarios.
- `GET /api/auth/{id}` — Obtener usuario por ID.
- `PUT /api/auth/{id}` — Actualizar usuario.
- `DELETE /api/auth/{id}` — Eliminar usuario.

### fleet-service (puerto 8083)

- `POST /api/flota/motorizados` — Crear motorizado.
- `POST /api/flota/camiones` — Crear camión.
- `POST /api/flota/livianos` — Crear vehículo liviano.
- `GET /api/flota` — Listar flota.
- `PATCH /api/flota/{id}/estado?estado=...` — Actualizar estado del vehículo.

### billing-service (puerto 8080)

- `POST /api/billing/calculate`
	- Request JSON: `{ "distanceKm": number, "durationMin": number }`
	- Respuesta: decimal con el valor calculado.
	- Fórmula: `2.50 + 1.20 * distanceKm + 0.25 * durationMin`.

- `POST /api/billing/invoices?customerId=...`
	- Request JSON: mismo formato que `/calculate`.
	- Crea una factura en estado `BORRADOR` y la persiste en `invoices`.
	- Respuesta: `{ id, customerId, amount, state, createdAt }`.

### pedido_service (puerto 8080)

Servicio en estado inicial. Estructura y conexión a base definidas; endpoints pendientes de implementación.

## API Gateway (Kong)

Este repositorio incluye una configuración de Kong en modo DB-less (declarativo) para enrutar peticiones hacia los microservicios. Los archivos están en `api-gateway/`:

- `api-gateway/docker-compose.yml`: definición del contenedor de Kong.
- `api-gateway/kong.yml`: configuración declarativa de servicios y rutas.

Kong se ejecuta en el puerto `8000` (proxy) y `8001` (Admin API). En macOS se usa `host.docker.internal` para que el contenedor alcance los servicios que corren en el host.

### Arranque

```zsh
docker compose -f "./api-gateway/docker-compose.yml" up -d
```

Comprobación del Admin API:

```zsh
curl -s http://localhost:8001/ | jq .
```

### Rutas configuradas

Según `api-gateway/kong.yml` (con `strip_path: false`, el path se preserva tal cual):

- pedido-service
	- Upstream: `http://host.docker.internal:8080`
	- Ruta: `/api/orders`

- auth-service
	- Upstream: `http://host.docker.internal:8081`
	- Ruta: `/api/auth`

- billing-service
	- Upstream: `http://host.docker.internal:8080`
	- Ruta: `/api/billing`

- fleet-service
	- Upstream: `http://host.docker.internal:8083`
	- Ruta: `/api/flota`

Nota: Asegúrese de que cada microservicio esté levantado en el puerto indicado antes de probar por el gateway.

### Pruebas por el proxy

```zsh
# Billing: calcular tarifa
curl -i -X POST http://localhost:8000/api/billing/calculate \
	-H "Content-Type: application/json" \
	-d '{"distanceKm":1,"durationMin":1}'

# Billing: crear factura
curl -i -X POST 'http://localhost:8000/api/billing/invoices?customerId=cliente123' \
	-H "Content-Type: application/json" \
	-d '{"distanceKm":4.5,"durationMin":12}'

# Auth: listar usuarios
curl -i http://localhost:8000/api/auth

# Fleet: listar flota
curl -i http://localhost:8000/api/flota
```

### Cómo ver el API Gateway

Kong expone un Admin API en `http://localhost:8001` para inspección y administración.

```zsh
# Ver estado general del Admin API
curl -i http://localhost:8001/

# Listar servicios cargados (DB-less: según kong.yml)
curl -s http://localhost:8001/services | jq .

# Listar rutas configuradas
curl -s http://localhost:8001/routes | jq .

# Probar el proxy (puerto 8000) hacia un servicio
curl -i -X POST http://localhost:8000/api/billing/calculate \
	-H "Content-Type: application/json" \
	-d '{"distanceKm":1,"durationMin":1}'
```

### Consideraciones

- `strip_path: false` en las rutas significa que el upstream recibirá el path completo con el prefijo `/api/...`, que coincide con los `@RequestMapping` definidos en los controladores.
- Si aparece `404 Not Found` a través de Kong, el servicio objetivo podría estar respondiendo 404; verifique que el endpoint exista y el servicio esté operativo.
- Si aparece `502 Bad Gateway`, normalmente el servicio objetivo no está accesible (proceso caído o puerto incorrecto).
- Compose puede advertir que el atributo `version` es obsoleto; es seguro eliminarlo del `docker-compose.yml` si desea evitar el mensaje.

## Verificación en la base de datos

Con pgAdmin (Docker, para `billing-service`):
- Servidor: `localhost:5433` (o `5432` si se usa instancia local).
- Base de datos: `delivery`.
- Esquema `public`, tabla `invoices`: verificar filas insertadas tras crear facturas.

Con `psql` en Docker:

```zsh
docker exec -it billing-service-postgres psql -U delivery_user -d delivery -c "SELECT id, customer_id, amount, state, created_at FROM invoices ORDER BY created_at DESC LIMIT 5;"
```

## Pruebas

Pendiente de agregar pruebas unitarias e integración (cálculo de tarifa, persistencia con Testcontainers/H2). Ejecución estándar:

```zsh
mvn test
```

## Consideraciones de arquitectura

- Cada servicio es independiente, con su propio ciclo de vida y despliegue.
- Se utiliza JPA con `ddl-auto=update` para gestionar el esquema en desarrollo.
- El estado de factura se almacena como texto (`BORRADOR`, `EMITIDA`, `PAGADA`, `CANCELADA`).
- No se realizaron cambios en un API Gateway dentro de este repositorio durante la implementación del servicio de facturación.

## Solución de problemas

- Error de autenticación PostgreSQL (por ejemplo, `FATAL: password authentication failed`):
	- Verificar usuario y contraseña configurados en `application.yaml` o variables de entorno.
	- Asegurar privilegios `CONNECT` sobre la base y `USAGE/CREATE` sobre el esquema `public`.

- Error de dialecto Hibernate (derivado de fallo de conexión):
	- Revisar conectividad y credenciales; corregir la autenticación.

- Puerto ocupado (5432):
	- Utilizar la opción de Docker en `5433` para desarrollo, o detener la instancia que ocupa `5432`.

## Licencia

Proyecto académico para la materia de Sistemas Distribuidos. Uso interno.