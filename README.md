----PRODUCT TRACKER API----
API REST para la búsqueda, seguimiento y monitorización automática de precios de productos en Amazon con alertas por correo electrónico ante bajadas de precio.

--QUICK START--
Aplicación completamente dockerizada. Para ejecutar rápidamente en local:
1. Clonar el repositorio
2. Cambiar el nombre al archivo .env.example por .env y configurar las variables de entorno con sus credenciales
3. Levantar los contenedores con docker compose up --build.

Backend: Java 25, Spring Boot 4, Spring Security, Spring Data JPA, JJWT (JSON Web Token), JavaMailSender, RestClient.
Base de Datos: PostgreSQL.
Infraestructura: Docker y Docker Compose.
Servicios Externos: RapidAPI (en concreto la api Real-Time Amazon Data API), Servidor SMTP (Mailtrap).

----ENDPOINTS----------------
-Autenticación:

POST /auth/register
Registra un nuevo usuario en el sistema.
Body: {"name": "string", "email": "string", "password": "string"}
Respuesta: {"token": "jwt_token"}

POST /auth/login
Autentica credenciales y genera token de sesión.
Body: {"email": "string", "password": "string"}
Respuesta: {"token": "jwt_token"}

-Productos:

GET /products/search?query={texto}
Consulta productos en tiempo real en la API de Amazon España.
Parámetros: query (string, requerido).

POST /products/track?asin={asin}
Guarda el producto en catálogo si no existe y lo añade a la lista de seguimiento del usuario autenticado.
Parámetros: asin (string, requerido).

GET /products/tracked
Devuelve la lista de productos que el usuario autenticado tiene en seguimiento.

DELETE /products/untrack?asin={asin}
Elimina el producto de la lista de seguimiento del usuario autenticado.
Parámetros: asin (string, requerido).

--PROCESOS EN SEGUNDO PLANO--
Consulta diariamente el catálogo de productos guardados para actualizar los precios. Si se detecta bajada de precio, actualiza la base de datos
y envía automáticamente un correo electrónico con el enlace directo de compra a los usuarios que siguen dicho artículo.
