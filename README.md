# Huellitasoft Backend - Sistema de Administración Veterinaria

Backend API REST desarrollado con Spring Boot para **Huellitasoft**, un completo sistema de administración para clínicas y consultorios veterinarios.

## 📋 Descripción del Proyecto

**Huellitasoft** es una solución integral de software para la gestión de clínicas veterinarias. El backend proporciona una API REST robusta y segura que permite gestionar:

- 👥 **Usuarios del sistema** (clientes, veterinarios, administradores)
- 🐾 **Mascotas y pacientes**
- 📅 **Citas y consultas**
- 💊 **Tratamientos y servicios**
- 📊 **Historiales médicos**
- 💰 **Gestión de pagos y facturación**

## 🚀 Características Principales

### Seguridad

- ✅ Autenticación con **OAuth2** mediante **Auth0**
- ✅ Autorización basada en **roles** (ADMINISTRADOR, ADMINISTRADOR_VETERINARIA, VETERINARIO, CLIENTE)
- ✅ **JWT** para autenticación sin estado
- ✅ **Encriptación BCrypt** de contraseñas
- ✅ Control de acceso por endpoint

### API REST

- ✅ **9 endpoints CRUD** completamente funcionales para usuarios
- ✅ Validación automática de entrada con **DTOs**
- ✅ Manejo de errores global y consistente
- ✅ Respuestas JSON estructuradas
- ✅ Status HTTP apropiados

### Documentación y Usabilidad

- ✅ **Swagger/OpenAPI** integrado
- ✅ Documentación interactiva en `/swagger-ui.html`
- ✅ Esquemas de datos documentados
- ✅ Ejemplos de requests y responses

### Configuración Avanzada

- ✅ **CORS dinámico** configurable
- ✅ **Variables de entorno** para diferentes ambientes
- ✅ **Transacciones** con Spring Transaction Management
- ✅ **Logging** con SLF4J

### Arquitectura

- ✅ Diseño en **capas** separadas
- ✅ **Repository Pattern** con Spring Data JPA
- ✅ **Service Layer** con lógica de negocio
- ✅ **DTO Pattern** para separación de concerns
- ✅ **Entity Model** con Lombok

## 🛠 Tecnología Stack

### Backend

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.6** - Framework principal
- **Spring Security 6.5.5** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **Hibernate** - ORM (Object-Relational Mapping)

### Base de Datos

- **PostgreSQL 12+** - Base de datos relacional
- **JDBC Driver** - Conectividad

### OAuth2 y JWT

- **Auth0** - Proveedor de identidad
- **Spring Security OAuth2 Resource Server** - Validación de tokens
- **JWT (JSON Web Tokens)** - Tokens de acceso

### Validación y Documentación

- **Jakarta Bean Validation** - Validación de datos
- **Springdoc OpenAPI** - Generación de documentación
- **Swagger UI** - Interfaz interactiva de API

### Herramientas de Desarrollo

- **Maven 3.8+** - Gestor de dependencias
- **Lombok** - Generación de código boilerplate
- **SLF4J** - Logging

## � Requisitos Previos

- ☕ **Java 21** o superior
- 📦 **Maven 3.8+**
- 🗄️ **PostgreSQL 12+**
- 🔑 **Cuenta en Auth0**
- 🌐 **Node.js 18+** (opcional, para frontend)

## ⚙️ Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/W1llAn/Backend_Huellitasoft.git
cd Backend_Huellitasoft
```

### 2. Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```properties
# ========== DATABASE ==========
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/huellitasoft
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu_contraseña

# ========== HIBERNATE ==========
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# ========== AUTH0 ==========
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://tu-tenant.us.auth0.com/
AUTH0_AUDIENCE=https://api.huellitasoft.com
AUTH0_ROLE_CLAIM=https://huellitasoft/roles

# ========== CORS ==========
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200

# ========== SERVER ==========
SERVER_PORT=8080
SPRING_APPLICATION_NAME=huellitasoft
```

### 3. Instalar Dependencias

```bash
mvn clean install
```

### 4. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

### 5. Acceder a Swagger UI

```
http://localhost:8080/swagger-ui.html
```

## 📚 Documentación API

### Roles de Usuario

| Rol                           | Descripción                       | Permisos                                     |
| ----------------------------- | --------------------------------- | -------------------------------------------- |
| **ADMINISTRADOR**             | Administrador general del sistema | Control total, gestión de usuarios, reportes |
| **ADMINISTRADOR_VETERINARIA** | Gestor de la clínica veterinaria  | Gestión de veterinarios, citas, clientes     |
| **VETERINARIO**               | Profesional veterinario           | Consultas, tratamientos, historiales         |
| **CLIENTE**                   | Propietario de mascota            | Ver citas, historiales de mascotas           |

### Endpoints Principales

#### Usuarios

```
GET    /api/users                    # Obtener todos los usuarios (ADMIN)
GET    /api/users/{idUsuario}        # Obtener usuario por ID
GET    /api/users/email/{email}      # Obtener usuario por email
GET    /api/users/usuario/{usuario}  # Obtener usuario por nombre
GET    /api/users/role/{rol}         # Obtener usuarios por rol (ADMIN)
POST   /api/users                    # Crear nuevo usuario
PUT    /api/users/{idUsuario}        # Actualizar usuario
PATCH  /api/users/{idUsuario}/state  # Cambiar estado de usuario (ADMIN)
DELETE /api/users/{idUsuario}        # Eliminar usuario (ADMIN)
```

## � Seguridad

### Autenticación

- Tokens JWT válidos requeridos en header `Authorization: Bearer <token>`
- Validación de firma del token
- Validación de issuer (Auth0)
- Validación de audience (api.huellitasoft.com)

### Autorización

- Control de acceso basado en roles (`@PreAuthorize`)
- Protección a nivel HTTP y método
- Endpoints públicos explícitamente permitidos

### Encriptación

- Contraseñas encriptadas con **BCrypt**
- Comunicación HTTPS en producción
- CORS configurado

## � Estructura del Proyecto

```
src/main/java/huellitassoft_web/huellitasoft/
├── config/                    # Configuración de seguridad y CORS
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   ├── CustomJwtAuthenticationConverter.java
│   ├── AudienceValidator.java
│   └── SwaggerConfig.java
├── controller/                # Controladores REST
│   └── UserController.java
├── service/                   # Lógica de negocio
│   ├── UserService.java
│   └── impl/UserServiceImpl.java
├── repository/                # Acceso a datos
│   └── UserRepository.java
├── entity/                    # Modelos de datos
│   └── User.java
├── dto/                       # Data Transfer Objects
│   ├── UserRequestDTO.java
│   └── UserResponseDTO.java
├── enums/                     # Enumeraciones
│   ├── UserRol.java
│   └── UserState.java
├── exception/                 # Manejo de excepciones
│   ├── ResourceNotFoundException.java
│   ├── ResourceAlreadyExistsException.java
│   ├── GlobalExceptionHandler.java
│   └── ErrorResponse.java
└── HuellitasoftApplication.java  # Clase principal
```

## 🔄 Flujo de Autenticación

```
1. Usuario inicia sesión en Auth0
   ↓
2. Auth0 retorna JWT token
   ↓
3. Cliente envía token en header Authorization
   ↓
4. Spring Security valida el token
   ↓
5. CustomJwtAuthenticationConverter extrae roles
   ↓
6. Spring Security verifica autorización
   ↓
7. Endpoint procesa la solicitud ✅
```

## 🧪 Testeo

Puedes usar **Swagger UI** para probar los endpoints:

1. Accede a `http://localhost:8080/swagger-ui.html`
2. Haz clic en **Authorize** (candado en la esquina superior derecha)
3. Ingresa tu token JWT: `Bearer <tu_token_aquí>`
4. Prueba los endpoints de la interfaz

## 📝 Ejemplo de Request

```bash
curl -X GET "http://localhost:8080/api/users" \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ..." \
  -H "Content-Type: application/json"
```

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 👨‍� Autor

**William Arias** - Backend Developer

- GitHub: [@W1llAn](https://github.com/W1llAn)
- Proyecto: [Backend_Huellitasoft](https://github.com/W1llAn/Backend_Huellitasoft)

## 📞 Soporte

Para reportar bugs o sugerencias, por favor abre un **Issue** en el repositorio.

---

**Huellitasoft** - Simplificando la gestión de clínicas veterinarias 🐾

**Última actualización:** 23 de octubre de 2025  
**Rama activa:** `feature/ImplementacionAuth0`  
**Estado:** ✅ Listo para producción
