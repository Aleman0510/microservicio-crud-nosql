# Microservicio CRUD con MongoDB

Microservicio REST desarrollado con Spring Boot 4.2, Java 21 y MongoDB. Gestiona productos mediante operaciones CRUD, validaciones de entrada y respuestas estructuradas ante errores.

## Tecnologías

- Java 21
- Maven
- Spring Boot
- Spring Web
- Spring Validation
- Spring Data MongoDB
- MongoDB 8
- Docker y Docker Compose
- JUnit 5 y Mockito
- JaCoCo
- SonarQube

## Requisitos

- Java 21
- Docker Desktop
- Maven integrado mediante el wrapper

## Ejecutar MongoDB

```powershell
docker compose up -d
```

MongoDB quedará disponible en `localhost:27017`.

## Ejecutar el microservicio

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.12"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd spring-boot:run
```

La aplicación utiliza el puerto `8080`.

## Endpoints

| Método | URL | Resultado |
|---|---|---|
| POST | `/api/v1/productos` | Crea un producto |
| GET | `/api/v1/productos` | Lista los productos |
| GET | `/api/v1/productos/{id}` | Busca un producto |
| PUT | `/api/v1/productos/{id}` | Actualiza un producto |
| DELETE | `/api/v1/productos/{id}` | Elimina un producto |

Ejemplo para crear:

```json
{
  "nombre": "Mouse",
  "descripcion": "Mouse inalámbrico",
  "precio": 350,
  "stock": 20
}
```

## Respuestas de error

- `400 Bad Request`: JSON o datos inválidos.
- `404 Not Found`: producto inexistente.
- `409 Conflict`: nombre duplicado.
- `500 Internal Server Error`: error interno.

Los errores se devuelven en JSON con estado, mensaje, ruta y fecha.

## Ejecutar pruebas

```powershell
.\mvnw.cmd clean verify
```

El informe HTML de cobertura se genera en:

```text
target/site/jacoco/index.html
```

## Ejecutar análisis local de SonarQube

```powershell
.\mvnw.cmd clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar "-Dsonar.host.url=http://localhost:9000" "-Dsonar.projectKey=microservicio-crud-nosql" "-Dsonar.token=TU_TOKEN"
```

El token de SonarQube no debe almacenarse en el repositorio.
