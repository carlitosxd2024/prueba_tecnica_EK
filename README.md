# Directorio de clientes

Aplicación sencilla para registrar y administrar clientes. Incluye una API REST en Java 17 con Spring Boot y PostgreSQL, además de una interfaz web en Vue 3.

## Ejecutar con Docker Compose

Se necesita Docker Desktop con Docker Compose. Desde la carpeta principal del proyecto:

```bash
docker compose up --build
```

Al iniciar por primera vez, Compose construye el frontend y la API, crea la base de datos y espera a que PostgreSQL esté disponible. La aplicación queda en [http://localhost:8081](http://localhost:8081).

Para detener los servicios, usa `Ctrl+C` y luego:

```bash
docker compose down
```

Los datos de PostgreSQL se conservan en el volumen `datos_clientes`. Para empezar de cero y borrar también los datos, ejecuta `docker compose down -v`.

### Variables de entorno

Los valores de desarrollo están en `docker-compose.yml`. Si quieres cambiarlos, copia `.env.example` a `.env` y actualiza las variables:

| Variable | Valor inicial | Uso |
| --- | --- | --- |
| `POSTGRES_DB` | `clientes` | Nombre de la base de datos |
| `POSTGRES_USER` | `clientes` | Usuario de PostgreSQL |
| `DB_PASSWORD` | `clientes` | Contraseña de PostgreSQL |
| `POSTGRES_PORT` | `5432` | Puerto de PostgreSQL en el equipo |
| `FRONTEND_PORT` | `8081` | Puerto de la aplicación web |

No uses la contraseña inicial en un entorno real. El archivo `.env` está excluido de Git.

## Desarrollo local

Para ejecutar cada parte por separado, necesitas Java 17, Maven, Node.js 20 o superior y una instancia de PostgreSQL.

### API

Configura `DB_URL`, `DB_USER` y `DB_PASSWORD` si PostgreSQL usa valores distintos a los locales. Después, desde `backend`:

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

### Frontend

Desde `frontend` instala las dependencias y levanta Vite:

```bash
npm install
npm run dev
```

Vite muestra la dirección local —normalmente `http://localhost:5173`— y reenvía las solicitudes `/clientes` a la API en el puerto 8080. Puedes cambiar el destino con `API_URL`.

## Endpoints

| Método | Ruta | Resultado |
| --- | --- | --- |
| `GET` | `/clientes` | Lista los clientes |
| `GET` | `/clientes/{id}` | Obtiene un cliente |
| `POST` | `/clientes` | Crea un cliente |
| `PUT` | `/clientes/{id}` | Actualiza un cliente |
| `DELETE` | `/clientes/{id}` | Elimina un cliente |

Ejemplo de cuerpo para crear o actualizar:

```json
{
  "nombres": "Ana",
  "apellidos": "García",
  "correo": "ana@ejemplo.com",
  "telefono": "+52 55 1234 5678"
}
```

La fecha de creación la asigna la API. Los errores incluyen un mensaje y su código HTTP; las validaciones responden con el detalle de los campos.

## Estructura del proyecto

```text
backend/
  src/main/java/com/carlos/clientes/
    cliente/     entidad, DTOs, repositorio, servicio y controlador
    common/      excepciones y respuestas de error
    config/      configuración CORS
  src/test/      pruebas del servicio de clientes
frontend/
  src/components/  formulario y tabla reutilizables
  src/services/    comunicación con la API
  src/             aplicación Vue y estilos
docker-compose.yml
```

## Consideraciones

- El controlador recibe y devuelve DTOs; la lógica del CRUD está en el servicio.
- Las entradas se validan con Jakarta Bean Validation. Los correos se normalizan a minúsculas y no se permite repetirlos.
- PostgreSQL conserva los registros entre reinicios mediante un volumen de Compose.
- La interfaz consume la API por la ruta `/clientes`; en Docker, Nginx la reenvía al servicio backend.
- Hibernate actualiza el esquema al iniciar para mantener simple la configuración de esta prueba. Para una aplicación con despliegues reales convendría gestionar cambios con migraciones.
