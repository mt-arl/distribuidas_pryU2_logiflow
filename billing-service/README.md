# Billing Service (mínimo)

Microservicio mínimo para cálculo de tarifa básica y generación de factura en estado BORRADOR.

Endpoints:
- POST /api/billing/calculate  -> body: {"distanceKm": number, "durationMin": number}  -> devuelve monto (BigDecimal)
- POST /api/billing/invoices   -> query param: customerId, body: same as calculate -> devuelve Invoice con estado BORRADOR

Compilar y levantar (desarrollo con Docker Postgres + pgAdmin):

1) Levantar Postgres y pgAdmin (desde el directorio `billing-service`):

```bash
docker compose up -d
```

Postgres (servidor principal): localhost:5432 (DB: delivery, user: delivery_user, pass: qwerty123)
pgAdmin: http://localhost:8085 (user: admin@local / admin) — agrega conexión usando host `host.docker.internal` o `localhost` y credenciales anteriores.

2) Compilar y ejecutar el servicio (apuntando a tu Postgres 5432 con credenciales fijadas):

```bash
mvn -f billing-service/pom.xml -DskipTests package
java -jar billing-service/target/billing-service-0.0.1-SNAPSHOT.jar
```

o en modo desarrollo desde el módulo:

```bash
cd billing-service
mvn -DskipTests spring-boot:run

Si tus credenciales no son `delivery_user/qwerty123`, exporta variables antes de arrancar:

Si necesitas usar otras credenciales distintas a `delivery_user/qwerty123`, puedes sobrescribirlas con variables de entorno antes de arrancar:

```bash
export BILLING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/delivery
export BILLING_DATASOURCE_USERNAME=<TU_USUARIO>
export BILLING_DATASOURCE_PASSWORD=<TU_PASSWORD>
```
```

3) Probar endpoints con Postman o curl:

Calcular tarifa:

POST http://localhost:8080/api/billing/calculate
Body JSON: {"distanceKm":4.5,"durationMin":12}

Crear factura (se persistirá en Postgres):

POST http://localhost:8080/api/billing/invoices?customerId=cliente123
Body JSON: {"distanceKm":4.5,"durationMin":12}

Revisa la tabla `invoices` en pgAdmin para ver las facturas creadas.

Notas:
- La aplicación toma la configuración de conexión de `application.yaml`. Puedes sobrescribir con variables de entorno `BILLING_DATASOURCE_URL`, `BILLING_DATASOURCE_USERNAME`, `BILLING_DATASOURCE_PASSWORD`.
- `hibernate.ddl-auto=update` creará/actualizará la tabla automáticamente al arrancar.

