# DOCUMENTACIÓN FUNCIONAL - HUELLITASOFT BACKEND

## ÍNDICE DE CONTENIDOS

1. [Gestión de Usuarios](#gestión-de-usuarios)
2. [Gestión de Clientes](#gestión-de-clientes)
3. [Gestión de Especies y Razas](#gestión-de-especies-y-razas)
4. [Gestión de Mascotas](#gestión-de-mascotas)
5. [Gestión de Citas](#gestión-de-citas)
6. [Gestión de Historiales Clínicos](#gestión-de-historiales-clínicos)
7. [Gestión de Consultas](#gestión-de-consultas)
8. [Gestión de Tratamientos](#gestión-de-tratamientos)
9. [Gestión de Vacunas](#gestión-de-vacunas)
10. [Gestión de Esquemas de Vacunación](#gestión-de-esquemas-de-vacunación)
11. [Gestión de Sucursales](#gestión-de-sucursales)
12. [Gestión de Notificaciones](#gestión-de-notificaciones)
13. [Patrones de Diseño Implementados](#patrones-de-diseño-implementados)
14. [Flujos de Trabajo Principales](#flujos-de-trabajo-principales)

---

## GESTIÓN DE USUARIOS

### Descripción General

El módulo de usuarios gestiona la autenticación, autorización y administración de todos los usuarios del sistema. Soporta integración con Auth0 para autenticación OAuth2 y gestión local de usuarios.

### Roles del Sistema

**ROLE_ADMINISTRADOR**

- Control total del sistema
- Gestión de todos los usuarios
- Acceso a todas las funcionalidades
- Configuración del sistema

**ROLE_ADMINISTRADOR_VETERINARIA**

- Administración de sucursales específicas
- Gestión de veterinarios de su sucursal
- Acceso a reportes y estadísticas
- Gestión de horarios de atención

**ROLE_VETERINARIO**

- Gestión de citas y consultas
- Creación de historiales médicos
- Prescripción de tratamientos
- Administración de vacunaciones
- Gestión de mascotas y clientes

**ROLE_CLIENTE**

- Visualización de sus mascotas
- Solicitud de citas
- Consulta de historiales de sus mascotas
- Recepción de notificaciones

### Endpoints de Usuario

#### POST /api/users/register-from-auth0

**Descripción:** Registro de usuario después de autenticación en Auth0.

**Acceso:** Público (no requiere autenticación)

**Request Body:**

```json
{
  "auth0Id": "auth0|123456789",
  "email": "usuario@ejemplo.com",
  "username": "usuario123",
  "rol": "ROLE_CLIENTE"
}
```

**Response 201 Created:**

```json
{
  "idUsuario": 1,
  "email": "usuario@ejemplo.com",
  "username": "usuario123",
  "rol": "ROLE_CLIENTE",
  "estado": "ACTIVO",
  "fechaCreacion": "2025-11-05T10:30:00"
}
```

**Validaciones:**

- Email único en el sistema
- Username único en el sistema
- Email con formato válido
- Rol válido del sistema

#### GET /api/users

**Descripción:** Obtener lista de todos los usuarios.

**Acceso:** Usuarios autenticados con roles administrativos

**Response 200 OK:**

```json
[
  {
    "idUsuario": 1,
    "email": "admin@huellitasoft.com",
    "username": "admin",
    "rol": "ROLE_ADMINISTRADOR",
    "estado": "ACTIVO",
    "fechaCreacion": "2025-01-01T00:00:00"
  },
  {
    "idUsuario": 2,
    "email": "vet@huellitasoft.com",
    "username": "veterinario1",
    "rol": "ROLE_VETERINARIO",
    "estado": "ACTIVO",
    "fechaCreacion": "2025-01-15T00:00:00"
  }
]
```

#### GET /api/users/{id}

**Descripción:** Obtener detalles de un usuario específico.

**Acceso:** Usuarios autenticados

**Path Parameters:**

- `id` (Long): ID del usuario

**Response 200 OK:**

```json
{
  "idUsuario": 1,
  "email": "usuario@ejemplo.com",
  "username": "usuario123",
  "rol": "ROLE_VETERINARIO",
  "estado": "ACTIVO",
  "fechaCreacion": "2025-11-05T10:30:00",
  "planContratado": "PREMIUM"
}
```

**Response 404 Not Found:**

```json
{
  "status": 404,
  "message": "Usuario no encontrado con ID: 1",
  "timestamp": "2025-11-05 10:30:45",
  "path": "/api/users/1"
}
```

#### PUT /api/users/{id}

**Descripción:** Actualizar información de un usuario.

**Acceso:** ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "email": "nuevo-email@ejemplo.com",
  "username": "nuevo-username",
  "rol": "ROLE_VETERINARIO",
  "estado": "ACTIVO",
  "planContratado": "PREMIUM"
}
```

**Response 200 OK:** Usuario actualizado

#### DELETE /api/users/{id}

**Descripción:** Eliminar un usuario del sistema (eliminación lógica).

**Acceso:** ADMINISTRADOR

**Response 204 No Content:** Usuario eliminado exitosamente

### Casos de Uso

**Caso 1: Registro de Usuario Nuevo**

1. Usuario se registra en Auth0
2. Frontend recibe token y datos de Auth0
3. Frontend llama a POST /api/users/register-from-auth0
4. Backend crea usuario en base de datos local
5. Se retorna información del usuario creado

**Caso 2: Cambio de Rol de Usuario**

1. Administrador identifica usuario a modificar
2. Llama a PUT /api/users/{id} con nuevo rol
3. Backend valida permisos
4. Actualiza rol en base de datos
5. Retorna usuario actualizado

---

## GESTIÓN DE CLIENTES

### Descripción General

Los clientes son los propietarios de las mascotas que reciben servicios veterinarios. Cada cliente debe tener una cuenta de usuario con rol ROLE_CLIENTE asociada.

### Endpoints de Cliente

#### POST /api/clientes

**Descripción:** Crear un nuevo cliente.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "nombres": "Juan Carlos",
  "apellidos": "García López",
  "documentoIdentidad": "12345678",
  "email": "juan.garcia@ejemplo.com",
  "telefono": "+34 600123456",
  "direccion": "Calle Principal 123, Madrid"
}
```

**Validaciones:**

- Nombres: requerido, máximo 100 caracteres
- Apellidos: requerido, máximo 100 caracteres
- Documento identidad: requerido, único, máximo 20 caracteres
- Email: requerido, único, formato válido, máximo 100 caracteres
- Teléfono: requerido, máximo 20 caracteres
- Dirección: requerido, máximo 255 caracteres

**Response 201 Created:**

```json
{
  "idCliente": 1,
  "nombres": "Juan Carlos",
  "apellidos": "García López",
  "documentoIdentidad": "12345678",
  "email": "juan.garcia@ejemplo.com",
  "telefono": "+34 600123456",
  "direccion": "Calle Principal 123, Madrid",
  "estado": "ACTIVO"
}
```

#### GET /api/clientes

**Descripción:** Obtener lista de todos los clientes.

**Acceso:** Usuarios autenticados

**Query Parameters (opcionales):**

- `estado`: Filtrar por estado (ACTIVO, INACTIVO, SUSPENDIDO)
- `search`: Búsqueda por nombre, apellido o documento

**Response 200 OK:**

```json
[
  {
    "idCliente": 1,
    "nombres": "Juan Carlos",
    "apellidos": "García López",
    "documentoIdentidad": "12345678",
    "email": "juan.garcia@ejemplo.com",
    "telefono": "+34 600123456",
    "direccion": "Calle Principal 123, Madrid",
    "estado": "ACTIVO"
  }
]
```

#### GET /api/clientes/{id}

**Descripción:** Obtener detalles de un cliente específico.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
{
  "idCliente": 1,
  "nombres": "Juan Carlos",
  "apellidos": "García López",
  "documentoIdentidad": "12345678",
  "email": "juan.garcia@ejemplo.com",
  "telefono": "+34 600123456",
  "direccion": "Calle Principal 123, Madrid",
  "estado": "ACTIVO",
  "mascotas": [
    {
      "idMascota": 1,
      "nombre": "Max",
      "especie": "Perro",
      "raza": "Labrador"
    }
  ]
}
```

#### PUT /api/clientes/{id}

**Descripción:** Actualizar información de un cliente.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "nombres": "Juan Carlos",
  "apellidos": "García López",
  "telefono": "+34 600999888",
  "direccion": "Nueva Dirección 456, Madrid",
  "estado": "ACTIVO"
}
```

**Response 200 OK:** Cliente actualizado

#### DELETE /api/clientes/{id}

**Descripción:** Cambiar estado de cliente a INACTIVO.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Response 204 No Content**

### Casos de Uso

**Caso 1: Registro de Cliente Nuevo**

1. Veterinario o recepcionista accede al formulario
2. Ingresa datos del cliente
3. Sistema valida datos únicos (email, documento)
4. Crea cliente con estado ACTIVO
5. Cliente puede registrar mascotas

**Caso 2: Suspensión de Cliente**

1. Administrador identifica cliente con problemas
2. Actualiza estado a SUSPENDIDO
3. Cliente no puede agendar nuevas citas
4. Se mantiene historial de mascotas

---

## GESTIÓN DE ESPECIES Y RAZAS

### Descripción General

Sistema de catálogo jerárquico para clasificar mascotas. Las especies son categorías principales (Perro, Gato, Ave, etc.) y las razas son subcategorías específicas de cada especie.

### Endpoints de Especies

#### GET /api/especies

**Descripción:** Obtener lista de todas las especies.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
[
  {
    "idEspecie": 1,
    "nombre": "Perro",
    "cantidadRazas": 150
  },
  {
    "idEspecie": 2,
    "nombre": "Gato",
    "cantidadRazas": 75
  }
]
```

#### POST /api/especies

**Descripción:** Crear una nueva especie.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "nombre": "Reptil"
}
```

**Response 201 Created:**

```json
{
  "idEspecie": 3,
  "nombre": "Reptil",
  "cantidadRazas": 0
}
```

#### GET /api/especies/{id}/razas

**Descripción:** Obtener todas las razas de una especie.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
[
  {
    "idRaza": 1,
    "nombre": "Labrador",
    "especie": {
      "idEspecie": 1,
      "nombre": "Perro"
    }
  },
  {
    "idRaza": 2,
    "nombre": "Golden Retriever",
    "especie": {
      "idEspecie": 1,
      "nombre": "Perro"
    }
  }
]
```

### Endpoints de Razas

#### GET /api/razas

**Descripción:** Obtener lista de todas las razas.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `especieId` (opcional): Filtrar por especie

**Response 200 OK:**

```json
[
  {
    "idRaza": 1,
    "nombre": "Labrador",
    "especie": {
      "idEspecie": 1,
      "nombre": "Perro"
    }
  }
]
```

#### POST /api/razas

**Descripción:** Crear una nueva raza.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "nombre": "Bulldog Francés",
  "idEspecie": 1
}
```

**Response 201 Created:**

```json
{
  "idRaza": 150,
  "nombre": "Bulldog Francés",
  "especie": {
    "idEspecie": 1,
    "nombre": "Perro"
  }
}
```

#### PUT /api/razas/{id}

**Descripción:** Actualizar una raza.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

#### DELETE /api/razas/{id}

**Descripción:** Eliminar una raza.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Nota:** No se puede eliminar si hay mascotas con esa raza.

---

## GESTIÓN DE MASCOTAS

### Descripción General

Las mascotas son los pacientes del sistema veterinario. Cada mascota pertenece a un cliente y tiene una raza específica.

### Endpoints de Mascotas

#### POST /api/mascotas

**Descripción:** Registrar una nueva mascota.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA, CLIENTE

**Request Body:**

```json
{
  "nombre": "Max",
  "fechaNacimiento": "2020-05-15",
  "sexo": "MACHO",
  "idCliente": 1,
  "idRaza": 1
}
```

**Validaciones:**

- Nombre: requerido
- Fecha de nacimiento: requerido, no puede ser futura
- Sexo: MACHO o HEMBRA
- Cliente: debe existir
- Raza: debe existir

**Response 201 Created:**

```json
{
  "idMascota": 1,
  "nombre": "Max",
  "fechaNacimiento": "2020-05-15",
  "edad": "4 años, 5 meses",
  "sexo": "MACHO",
  "estado": true,
  "cliente": {
    "idCliente": 1,
    "nombres": "Juan Carlos",
    "apellidos": "García López"
  },
  "raza": {
    "idRaza": 1,
    "nombre": "Labrador",
    "especie": "Perro"
  }
}
```

#### GET /api/mascotas

**Descripción:** Obtener lista de mascotas.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `clienteId`: Filtrar por cliente
- `razaId`: Filtrar por raza
- `especieId`: Filtrar por especie

**Response 200 OK:**

```json
[
  {
    "idMascota": 1,
    "nombre": "Max",
    "fechaNacimiento": "2020-05-15",
    "edad": "4 años, 5 meses",
    "sexo": "MACHO",
    "estado": true,
    "cliente": {
      "idCliente": 1,
      "nombreCompleto": "Juan Carlos García López"
    },
    "raza": {
      "nombre": "Labrador",
      "especie": "Perro"
    }
  }
]
```

#### GET /api/mascotas/{id}

**Descripción:** Obtener detalles completos de una mascota.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
{
  "idMascota": 1,
  "nombre": "Max",
  "fechaNacimiento": "2020-05-15",
  "edad": "4 años, 5 meses",
  "sexo": "MACHO",
  "estado": true,
  "cliente": {
    "idCliente": 1,
    "nombres": "Juan Carlos",
    "apellidos": "García López",
    "telefono": "+34 600123456",
    "email": "juan.garcia@ejemplo.com"
  },
  "raza": {
    "idRaza": 1,
    "nombre": "Labrador",
    "especie": "Perro"
  },
  "historiaClinica": {
    "idHistoria": 1,
    "numero": "HC-2025-001",
    "estado": "ACTIVO"
  },
  "proximasCitas": [
    {
      "idCita": 5,
      "fechaHora": "2025-11-10T10:00:00",
      "motivo": "Control general"
    }
  ]
}
```

#### PUT /api/mascotas/{id}

**Descripción:** Actualizar información de mascota.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA, CLIENTE (solo sus mascotas)

**Request Body:**

```json
{
  "nombre": "Max",
  "fechaNacimiento": "2020-05-15",
  "sexo": "MACHO",
  "estado": true,
  "idRaza": 1
}
```

**Response 200 OK:** Mascota actualizada

#### DELETE /api/mascotas/{id}

**Descripción:** Cambiar estado de mascota a inactivo.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Response 204 No Content**

### Casos de Uso

**Caso 1: Registro de Mascota Nueva**

1. Cliente solicita servicio veterinario
2. Recepcionista registra datos de la mascota
3. Sistema valida cliente y raza existentes
4. Crea mascota con estado activo
5. Sistema automáticamente crea historial clínico
6. Mascota lista para agendar citas

**Caso 2: Actualización de Información**

1. Propietario actualiza datos (ej. cambio de nombre)
2. Sistema valida permisos (propietario o staff)
3. Actualiza información
4. Mantiene historial de cambios

---

## GESTIÓN DE CITAS

### Descripción General

Sistema de agendamiento de citas veterinarias con validación de disponibilidad, horarios de sucursal y estados de cita.

### Estados de Cita

- **PENDIENTE:** Cita creada pero no confirmada
- **CONFIRMADA:** Cliente ha confirmado asistencia
- **CANCELADA:** Cita cancelada por cliente o clínica
- **ATENDIDA:** Cita completada satisfactoriamente
- **NO_ASISTIO:** Cliente no asistió a cita confirmada

### Endpoints de Citas

#### POST /api/citas

**Descripción:** Crear una nueva cita.

**Acceso:** Usuarios autenticados

**Request Body:**

```json
{
  "fechaHora": "2025-11-10T10:00:00",
  "motivo": "Control general y vacunación",
  "idCliente": 1,
  "idMascota": 1,
  "idVeterinario": 2,
  "idSucursal": 1
}
```

**Validaciones:**

- Fecha y hora: no puede ser pasada
- Motivo: requerido, máximo 255 caracteres
- Cliente: debe existir y estar activo
- Mascota: debe existir y pertenecer al cliente
- Veterinario: debe existir y tener rol VETERINARIO
- Sucursal: debe existir y estar activa
- Horario: debe estar dentro del horario de atención de la sucursal
- Disponibilidad: veterinario no debe tener otra cita en el mismo horario

**Response 201 Created:**

```json
{
  "idCita": 1,
  "fechaHora": "2025-11-10T10:00:00",
  "estado": "PENDIENTE",
  "motivo": "Control general y vacunación",
  "cliente": {
    "idCliente": 1,
    "nombreCompleto": "Juan Carlos García López",
    "telefono": "+34 600123456"
  },
  "mascota": {
    "idMascota": 1,
    "nombre": "Max",
    "raza": "Labrador"
  },
  "veterinario": {
    "idUsuario": 2,
    "nombre": "Dr. María Pérez"
  },
  "sucursal": {
    "idSucursal": 1,
    "nombre": "Sucursal Centro",
    "direccion": "Av. Principal 100"
  }
}
```

**Response 400 Bad Request:**

```json
{
  "status": 400,
  "message": "El veterinario ya tiene una cita programada en ese horario",
  "timestamp": "2025-11-05 10:30:45",
  "path": "/api/citas"
}
```

#### GET /api/citas

**Descripción:** Obtener lista de citas.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `clienteId`: Filtrar por cliente
- `mascotaId`: Filtrar por mascota
- `veterinarioId`: Filtrar por veterinario
- `sucursalId`: Filtrar por sucursal
- `estado`: Filtrar por estado
- `fechaDesde`: Filtrar desde fecha (formato: YYYY-MM-DD)
- `fechaHasta`: Filtrar hasta fecha (formato: YYYY-MM-DD)

**Response 200 OK:**

```json
[
  {
    "idCita": 1,
    "fechaHora": "2025-11-10T10:00:00",
    "estado": "CONFIRMADA",
    "motivo": "Control general",
    "cliente": {
      "nombreCompleto": "Juan Carlos García López"
    },
    "mascota": {
      "nombre": "Max"
    },
    "veterinario": {
      "nombre": "Dr. María Pérez"
    }
  }
]
```

#### GET /api/citas/{id}

**Descripción:** Obtener detalles de una cita específica.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
{
  "idCita": 1,
  "fechaHora": "2025-11-10T10:00:00",
  "estado": "CONFIRMADA",
  "motivo": "Control general y vacunación",
  "cliente": {
    "idCliente": 1,
    "nombreCompleto": "Juan Carlos García López",
    "telefono": "+34 600123456",
    "email": "juan.garcia@ejemplo.com"
  },
  "mascota": {
    "idMascota": 1,
    "nombre": "Max",
    "edad": "4 años, 5 meses",
    "raza": "Labrador",
    "sexo": "MACHO"
  },
  "veterinario": {
    "idUsuario": 2,
    "nombre": "Dr. María Pérez",
    "email": "maria.perez@clinica.com"
  },
  "sucursal": {
    "idSucursal": 1,
    "nombre": "Sucursal Centro",
    "direccion": "Av. Principal 100",
    "telefono": "+34 900123456"
  }
}
```

#### PUT /api/citas/{id}

**Descripción:** Actualizar una cita.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "fechaHora": "2025-11-10T11:00:00",
  "estado": "CONFIRMADA",
  "motivo": "Control general y vacunación actualizado",
  "idVeterinario": 2,
  "idSucursal": 1
}
```

**Response 200 OK:** Cita actualizada

#### DELETE /api/citas/{id}

**Descripción:** Cancelar una cita (cambia estado a CANCELADA).

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Response 204 No Content**

#### GET /api/citas/disponibilidad

**Descripción:** Consultar disponibilidad de horarios.

**Query Parameters:**

- `veterinarioId`: ID del veterinario
- `sucursalId`: ID de la sucursal
- `fecha`: Fecha a consultar (YYYY-MM-DD)

**Response 200 OK:**

```json
{
  "fecha": "2025-11-10",
  "horariosDisponibles": ["09:00:00", "09:30:00", "10:00:00", "11:00:00"],
  "horariosOcupados": ["10:30:00", "11:30:00"]
}
```

### Casos de Uso

**Caso 1: Agendar Cita Normal**

1. Cliente o recepcionista selecciona mascota
2. Elige veterinario y sucursal
3. Consulta disponibilidad
4. Selecciona fecha y hora disponible
5. Ingresa motivo de consulta
6. Sistema valida todos los criterios
7. Crea cita con estado PENDIENTE
8. Envía notificación por email
9. Envía notificación WebSocket

**Caso 2: Confirmar Cita**

1. Cliente recibe notificación de cita pendiente
2. Accede al sistema
3. Confirma asistencia
4. Sistema cambia estado a CONFIRMADA
5. Envía recordatorio 24h antes

**Caso 3: Cancelar Cita**

1. Usuario solicita cancelación
2. Sistema verifica permisos
3. Cambia estado a CANCELADA
4. Libera horario del veterinario
5. Notifica a todas las partes
6. Registra razón de cancelación

**Caso 4: Marcar Cita Atendida**

1. Veterinario completa consulta
2. Sistema verifica que exista registro de consulta
3. Cambia estado a ATENDIDA
4. Actualiza historial clínico

---

## GESTIÓN DE HISTORIALES CLÍNICOS

### Descripción General

Cada mascota tiene un historial clínico único que contiene todas sus consultas médicas. El historial se crea automáticamente al registrar la mascota.

### Estados de Historial

- **ACTIVO:** Historial en uso actual
- **CERRADO:** Historial cerrado (mascota fallecida o transferida)

### Endpoints de Historiales Clínicos

#### GET /api/historiales-clinicos

**Descripción:** Obtener lista de historiales.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `mascotaId`: Filtrar por mascota
- `clienteId`: Filtrar por cliente
- `estado`: Filtrar por estado

**Response 200 OK:**

```json
[
  {
    "idHistoria": 1,
    "numero": "HC-2025-001",
    "estado": "ACTIVO",
    "mascota": {
      "idMascota": 1,
      "nombre": "Max",
      "raza": "Labrador"
    },
    "cantidadConsultas": 5,
    "ultimaConsulta": "2025-10-15T10:00:00"
  }
]
```

#### GET /api/historiales-clinicos/{id}

**Descripción:** Obtener detalles completos de un historial.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
{
  "idHistoria": 1,
  "numero": "HC-2025-001",
  "estado": "ACTIVO",
  "mascota": {
    "idMascota": 1,
    "nombre": "Max",
    "fechaNacimiento": "2020-05-15",
    "edad": "4 años, 5 meses",
    "sexo": "MACHO",
    "raza": "Labrador",
    "cliente": {
      "nombreCompleto": "Juan Carlos García López",
      "telefono": "+34 600123456"
    }
  },
  "consultas": [
    {
      "idConsulta": 1,
      "fechaHora": "2025-10-15T10:00:00",
      "motivo": "Control general",
      "diagnostico": "Animal sano",
      "veterinario": {
        "nombre": "Dr. María Pérez"
      }
    }
  ]
}
```

#### POST /api/historiales-clinicos

**Descripción:** Crear un nuevo historial clínico.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Nota:** Normalmente se crea automáticamente al registrar una mascota.

**Request Body:**

```json
{
  "idMascota": 1,
  "numero": "HC-2025-001"
}
```

**Response 201 Created:**

```json
{
  "idHistoria": 1,
  "numero": "HC-2025-001",
  "estado": "ACTIVO",
  "mascota": {
    "idMascota": 1,
    "nombre": "Max"
  }
}
```

#### PUT /api/historiales-clinicos/{id}

**Descripción:** Actualizar historial (principalmente para cerrar).

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "estado": "CERRADO"
}
```

**Response 200 OK:** Historial actualizado

---

## GESTIÓN DE CONSULTAS

### Descripción General

Las consultas son los registros médicos de las visitas veterinarias. Pertenecen a un historial clínico y pueden tener múltiples tratamientos asociados.

### Endpoints de Consultas

#### POST /api/consultas

**Descripción:** Registrar una nueva consulta médica.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "idHistorial": 1,
  "fechaHora": "2025-11-05T10:00:00",
  "motivo": "Control general y vacunación",
  "idVeterinario": 2,
  "diagnostico": "Animal en buen estado de salud. Se aplica vacuna antirrábica.",
  "indicaciones": "Reposo por 24 horas. Observar reacción a la vacuna."
}
```

**Validaciones:**

- Historial: debe existir y estar activo
- Fecha y hora: requerida
- Motivo: requerido, máximo 500 caracteres
- Veterinario: debe existir
- Diagnóstico: opcional, máximo 1000 caracteres
- Indicaciones: opcional, máximo 1000 caracteres

**Response 201 Created:**

```json
{
  "idConsulta": 1,
  "fechaHora": "2025-11-05T10:00:00",
  "motivo": "Control general y vacunación",
  "diagnostico": "Animal en buen estado de salud. Se aplica vacuna antirrábica.",
  "indicaciones": "Reposo por 24 horas. Observar reacción a la vacuna.",
  "veterinario": {
    "idUsuario": 2,
    "nombre": "Dr. María Pérez"
  },
  "historialClinico": {
    "idHistoria": 1,
    "numero": "HC-2025-001",
    "mascota": {
      "nombre": "Max"
    }
  }
}
```

#### GET /api/consultas

**Descripción:** Obtener lista de consultas.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `historialId`: Filtrar por historial clínico
- `mascotaId`: Filtrar por mascota
- `veterinarioId`: Filtrar por veterinario
- `fechaDesde`: Filtrar desde fecha
- `fechaHasta`: Filtrar hasta fecha

**Response 200 OK:**

```json
[
  {
    "idConsulta": 1,
    "fechaHora": "2025-11-05T10:00:00",
    "motivo": "Control general",
    "diagnostico": "Animal sano",
    "mascota": {
      "nombre": "Max"
    },
    "veterinario": {
      "nombre": "Dr. María Pérez"
    }
  }
]
```

#### GET /api/consultas/{id}

**Descripción:** Obtener detalles completos de una consulta.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
{
  "idConsulta": 1,
  "fechaHora": "2025-11-05T10:00:00",
  "motivo": "Control general y vacunación",
  "diagnostico": "Animal en buen estado de salud. Se aplica vacuna antirrábica.",
  "indicaciones": "Reposo por 24 horas. Observar reacción a la vacuna.",
  "veterinario": {
    "idUsuario": 2,
    "nombre": "Dr. María Pérez",
    "email": "maria.perez@clinica.com"
  },
  "historialClinico": {
    "idHistoria": 1,
    "numero": "HC-2025-001",
    "mascota": {
      "idMascota": 1,
      "nombre": "Max",
      "edad": "4 años, 5 meses",
      "raza": "Labrador"
    }
  },
  "tratamientos": [
    {
      "idTratamiento": 1,
      "duracionDias": 7,
      "observaciones": "Antibiótico cada 12 horas"
    }
  ]
}
```

#### PUT /api/consultas/{id}

**Descripción:** Actualizar información de consulta.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "motivo": "Control general y vacunación",
  "diagnostico": "Diagnóstico actualizado",
  "indicaciones": "Indicaciones actualizadas"
}
```

**Response 200 OK:** Consulta actualizada

#### DELETE /api/consultas/{id}

**Descripción:** Eliminar una consulta.

**Acceso:** ADMINISTRADOR

**Nota:** Operación sensible, elimina también tratamientos asociados.

**Response 204 No Content**

### Casos de Uso

**Caso 1: Registro de Consulta Completa**

1. Veterinario atiende cita
2. Examina paciente
3. Registra motivo de consulta
4. Ingresa diagnóstico detallado
5. Especifica indicaciones y recomendaciones
6. Prescribe tratamientos si es necesario
7. Sistema asocia consulta al historial clínico
8. Genera registro para reportes

**Caso 2: Consulta de Seguimiento**

1. Veterinario revisa consultas anteriores
2. Crea nueva consulta de seguimiento
3. Referencia tratamientos previos
4. Evalúa evolución del paciente
5. Actualiza diagnóstico
6. Modifica tratamiento si es necesario

---

## GESTIÓN DE TRATAMIENTOS

### Descripción General

Los tratamientos son prescripciones médicas asociadas a una consulta específica. Pueden ser medicamentos, terapias, o procedimientos con duración definida.

### Endpoints de Tratamientos

#### POST /api/tratamientos

**Descripción:** Crear un nuevo tratamiento.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "idConsulta": 1,
  "idMascota": 1,
  "duracionDias": 7,
  "observaciones": "Amoxicilina 500mg cada 12 horas con alimento. Completar tratamiento completo."
}
```

**Validaciones:**

- Consulta: debe existir
- Mascota: debe existir
- Duración: requerida, mayor a 0
- Observaciones: opcional, máximo 1000 caracteres

**Response 201 Created:**

```json
{
  "idTratamiento": 1,
  "duracionDias": 7,
  "observaciones": "Amoxicilina 500mg cada 12 horas con alimento. Completar tratamiento completo.",
  "estado": true,
  "fechaInicio": "2025-11-05",
  "fechaFin": "2025-11-12",
  "consulta": {
    "idConsulta": 1,
    "fechaHora": "2025-11-05T10:00:00"
  },
  "mascota": {
    "idMascota": 1,
    "nombre": "Max"
  }
}
```

#### GET /api/tratamientos

**Descripción:** Obtener lista de tratamientos.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `mascotaId`: Filtrar por mascota
- `consultaId`: Filtrar por consulta
- `estado`: Filtrar por estado (activo/inactivo)
- `fechaDesde`: Filtrar desde fecha
- `fechaHasta`: Filtrar hasta fecha

**Response 200 OK:**

```json
[
  {
    "idTratamiento": 1,
    "duracionDias": 7,
    "observaciones": "Amoxicilina 500mg cada 12 horas",
    "estado": true,
    "fechaInicio": "2025-11-05",
    "fechaFin": "2025-11-12",
    "mascota": {
      "nombre": "Max"
    },
    "veterinario": {
      "nombre": "Dr. María Pérez"
    }
  }
]
```

#### GET /api/tratamientos/{id}

**Descripción:** Obtener detalles de un tratamiento.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
{
  "idTratamiento": 1,
  "duracionDias": 7,
  "observaciones": "Amoxicilina 500mg cada 12 horas con alimento. Completar tratamiento completo.",
  "estado": true,
  "fechaInicio": "2025-11-05",
  "fechaFin": "2025-11-12",
  "diasRestantes": 5,
  "consulta": {
    "idConsulta": 1,
    "fechaHora": "2025-11-05T10:00:00",
    "motivo": "Infección respiratoria",
    "diagnostico": "Bronquitis bacteriana"
  },
  "mascota": {
    "idMascota": 1,
    "nombre": "Max",
    "cliente": {
      "nombreCompleto": "Juan Carlos García López",
      "telefono": "+34 600123456"
    }
  }
}
```

#### PUT /api/tratamientos/{id}

**Descripción:** Actualizar tratamiento.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "duracionDias": 10,
  "observaciones": "Tratamiento extendido. Amoxicilina 500mg cada 12 horas",
  "estado": true
}
```

**Response 200 OK:** Tratamiento actualizado

#### DELETE /api/tratamientos/{id}

**Descripción:** Desactivar tratamiento.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Response 204 No Content**

### Casos de Uso

**Caso 1: Prescripción de Tratamiento**

1. Veterinario completa diagnóstico
2. Determina tratamiento necesario
3. Especifica medicamento y posología
4. Define duración del tratamiento
5. Agrega observaciones importantes
6. Sistema calcula fecha de fin
7. Notifica a propietario

**Caso 2: Seguimiento de Tratamiento**

1. Cliente consulta tratamientos activos
2. Sistema muestra días restantes
3. Cliente puede ver observaciones
4. Veterinario puede revisar adherencia
5. Se pueden agregar notas de seguimiento

---

## GESTIÓN DE VACUNAS

### Descripción General

Catálogo de vacunas disponibles en la clínica veterinaria. Cada vacuna es específica para una especie.

### Endpoints de Vacunas

#### POST /api/vacunas

**Descripción:** Registrar una nueva vacuna.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "nombre": "Vacuna Antirrábica",
  "descripcion": "Vacuna obligatoria contra la rabia. Protección anual.",
  "idEspecie": 1
}
```

**Validaciones:**

- Nombre: requerido, único, máximo 100 caracteres
- Descripción: requerida
- Especie: debe existir

**Response 201 Created:**

```json
{
  "idVacuna": 1,
  "nombre": "Vacuna Antirrábica",
  "descripcion": "Vacuna obligatoria contra la rabia. Protección anual.",
  "especie": {
    "idEspecie": 1,
    "nombre": "Perro"
  }
}
```

#### GET /api/vacunas

**Descripción:** Obtener lista de vacunas.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `especieId`: Filtrar por especie

**Response 200 OK:**

```json
[
  {
    "idVacuna": 1,
    "nombre": "Vacuna Antirrábica",
    "descripcion": "Vacuna obligatoria contra la rabia",
    "especie": {
      "idEspecie": 1,
      "nombre": "Perro"
    },
    "esquemas": 1
  },
  {
    "idVacuna": 2,
    "nombre": "Vacuna Múltiple (Pentavalente)",
    "descripcion": "Protege contra distemper, hepatitis, leptospirosis, parvovirus, parainfluenza",
    "especie": {
      "idEspecie": 1,
      "nombre": "Perro"
    },
    "esquemas": 3
  }
]
```

#### GET /api/vacunas/{id}

**Descripción:** Obtener detalles de una vacuna.

**Acceso:** Usuarios autenticados

**Response 200 OK:**

```json
{
  "idVacuna": 2,
  "nombre": "Vacuna Múltiple (Pentavalente)",
  "descripcion": "Protege contra distemper, hepatitis, leptospirosis, parvovirus, parainfluenza",
  "especie": {
    "idEspecie": 1,
    "nombre": "Perro"
  },
  "esquemasVacunacion": [
    {
      "idEsquema": 1,
      "dosisNumero": 1,
      "edadSemanas": 6,
      "observaciones": "Primera dosis"
    },
    {
      "idEsquema": 2,
      "dosisNumero": 2,
      "edadSemanas": 10,
      "observaciones": "Segunda dosis"
    },
    {
      "idEsquema": 3,
      "dosisNumero": 3,
      "edadSemanas": 14,
      "observaciones": "Tercera dosis"
    }
  ]
}
```

#### PUT /api/vacunas/{id}

**Descripción:** Actualizar vacuna.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

#### DELETE /api/vacunas/{id}

**Descripción:** Eliminar vacuna.

**Acceso:** ADMINISTRADOR

**Nota:** No se puede eliminar si tiene esquemas de vacunación asociados.

---

## GESTIÓN DE ESQUEMAS DE VACUNACIÓN

### Descripción General

Los esquemas de vacunación definen el plan de dosis para cada vacuna, especificando el número de dosis y la edad recomendada para cada aplicación.

### Endpoints de Esquemas de Vacunación

#### POST /api/esquemas-vacunacion

**Descripción:** Crear un nuevo esquema de vacunación.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "idVacuna": 2,
  "dosisNumero": 1,
  "edadSemanas": 6,
  "observaciones": "Primera dosis de vacuna múltiple"
}
```

**Validaciones:**

- Vacuna: debe existir
- Dosis número: requerido, mayor a 0
- Edad en semanas: requerida, mayor a 0
- Constraint único: vacuna + número de dosis

**Response 201 Created:**

```json
{
  "idEsquema": 1,
  "dosisNumero": 1,
  "edadSemanas": 6,
  "observaciones": "Primera dosis de vacuna múltiple",
  "vacuna": {
    "idVacuna": 2,
    "nombre": "Vacuna Múltiple (Pentavalente)"
  }
}
```

#### GET /api/esquemas-vacunacion

**Descripción:** Obtener lista de esquemas.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `vacunaId`: Filtrar por vacuna

**Response 200 OK:**

```json
[
  {
    "idEsquema": 1,
    "dosisNumero": 1,
    "edadSemanas": 6,
    "observaciones": "Primera dosis",
    "vacuna": {
      "nombre": "Vacuna Múltiple"
    }
  },
  {
    "idEsquema": 2,
    "dosisNumero": 2,
    "edadSemanas": 10,
    "observaciones": "Segunda dosis",
    "vacuna": {
      "nombre": "Vacuna Múltiple"
    }
  }
]
```

#### GET /api/esquemas-vacunacion/{id}

**Descripción:** Obtener detalles de un esquema.

**Acceso:** Usuarios autenticados

#### PUT /api/esquemas-vacunacion/{id}

**Descripción:** Actualizar esquema.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

#### DELETE /api/esquemas-vacunacion/{id}

**Descripción:** Eliminar esquema.

**Acceso:** ADMINISTRADOR

### Asignación de Esquemas a Mascotas

#### POST /api/mascota-esquemas

**Descripción:** Asignar esquema de vacunación a una mascota.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "idMascota": 1,
  "idEsquema": 1,
  "estado": "PENDIENTE"
}
```

**Response 201 Created:**

```json
{
  "idMascotaEsquema": 1,
  "estado": "PENDIENTE",
  "mascota": {
    "idMascota": 1,
    "nombre": "Max",
    "edad": "6 semanas"
  },
  "esquema": {
    "idEsquema": 1,
    "vacuna": "Vacuna Múltiple",
    "dosisNumero": 1,
    "edadSemanas": 6
  }
}
```

#### GET /api/mascota-esquemas

**Descripción:** Obtener esquemas asignados a mascotas.

**Query Parameters:**

- `mascotaId`: Filtrar por mascota
- `estado`: Filtrar por estado (PENDIENTE, EN_PROCESO, COMPLETADO)

### Registro de Vacunación

#### POST /api/vacunacion-mascota

**Descripción:** Registrar aplicación de vacuna.

**Acceso:** VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA

**Request Body:**

```json
{
  "idMascotaEsquema": 1,
  "fechaAplicacion": "2025-11-05",
  "proximaDosis": "2025-12-05",
  "observaciones": "Sin reacciones adversas"
}
```

**Response 201 Created:**

```json
{
  "idVacunacion": 1,
  "fechaAplicacion": "2025-11-05",
  "proximaDosis": "2025-12-05",
  "observaciones": "Sin reacciones adversas",
  "mascotaEsquema": {
    "idMascotaEsquema": 1,
    "estado": "COMPLETADO",
    "mascota": {
      "nombre": "Max"
    },
    "esquema": {
      "vacuna": "Vacuna Múltiple",
      "dosisNumero": 1
    }
  }
}
```

---

## GESTIÓN DE SUCURSALES

### Descripción General

Sistema de gestión de múltiples sucursales o locaciones de la clínica veterinaria, con sus horarios de atención.

### Endpoints de Sucursales

#### POST /api/sucursales

**Descripción:** Crear una nueva sucursal.

**Acceso:** ADMINISTRADOR

**Request Body:**

```json
{
  "nombre": "Sucursal Centro",
  "direccion": "Av. Principal 100, Madrid",
  "idAdministrador": 3
}
```

**Response 201 Created:**

```json
{
  "idSucursal": 1,
  "nombre": "Sucursal Centro",
  "direccion": "Av. Principal 100, Madrid",
  "estado": "ACTIVO",
  "administrador": {
    "idUsuario": 3,
    "nombre": "Administrador Sucursal"
  }
}
```

#### GET /api/sucursales

**Descripción:** Obtener lista de sucursales.

**Acceso:** Usuarios autenticados

**Query Parameters:**

- `estado`: Filtrar por estado

**Response 200 OK:**

```json
[
  {
    "idSucursal": 1,
    "nombre": "Sucursal Centro",
    "direccion": "Av. Principal 100, Madrid",
    "estado": "ACTIVO",
    "horarios": [
      {
        "dia": "LUNES",
        "turno": "MAÑANA",
        "horaInicio": "09:00",
        "horaFin": "13:00"
      }
    ]
  }
]
```

#### POST /api/sucursales/{id}/horarios

**Descripción:** Agregar horario de atención a sucursal.

**Request Body:**

```json
{
  "dia": "LUNES",
  "turno": "MAÑANA",
  "horaInicio": "09:00:00",
  "horaFin": "13:00:00"
}
```

---

## GESTIÓN DE NOTIFICACIONES

### Descripción General

Sistema de notificaciones multicanal (WebSocket y Email) para mantener informados a usuarios y clientes.

### Tipos de Notificaciones

- **CITA:** Recordatorios y cambios de citas
- **VACUNA:** Recordatorios de vacunación
- **TRATAMIENTO:** Seguimiento de tratamientos
- **SISTEMA:** Notificaciones administrativas

### Endpoints de Notificaciones

#### GET /api/notificaciones

**Descripción:** Obtener notificaciones del usuario autenticado.

**Query Parameters:**

- `leida`: Filtrar por leídas/no leídas
- `tipo`: Filtrar por tipo

**Response 200 OK:**

```json
[
  {
    "idNotificacion": 1,
    "titulo": "RECORDATORIO_CITA",
    "asunto": "Recordatorio de cita",
    "mensaje": "Tiene una cita programada para mañana a las 10:00",
    "tipo": "CITA",
    "leida": false,
    "fechaCreacion": "2025-11-04T10:00:00",
    "cita": {
      "idCita": 1,
      "fechaHora": "2025-11-05T10:00:00"
    }
  }
]
```

#### PUT /api/notificaciones/{id}/marcar-leida

**Descripción:** Marcar notificación como leída.

**Response 200 OK**

---

## PATRONES DE DISEÑO IMPLEMENTADOS

### 1. Repository Pattern

**Descripción:** Abstracción de la capa de acceso a datos.

**Implementación:**

- Interfaces que extienden JpaRepository
- Separación entre lógica de negocio y persistencia
- Queries personalizadas cuando sea necesario

### 2. Service Layer Pattern

**Descripción:** Capa de servicios con lógica de negocio.

**Implementación:**

- Interfaces de servicio definen contrato
- Implementaciones contienen lógica de negocio
- Gestión de transacciones con @Transactional

### 3. DTO Pattern (Data Transfer Object)

**Descripción:** Objetos para transferencia de datos entre capas.

**Implementación:**

- DTOs de request para entrada de datos
- DTOs de response para salida de datos
- Separación del modelo de dominio de la API

### 4. Builder Pattern

**Descripción:** Construcción de objetos complejos.

**Implementación:**

- Lombok @Builder en entidades y DTOs
- Facilita creación de objetos de prueba
- Mejora legibilidad del código

### 5. Singleton Pattern

**Descripción:** Instancia única de configuraciones.

**Implementación:**

- Beans de Spring (@Bean, @Component)
- Configuraciones del sistema
- Servicios compartidos

---

## FLUJOS DE TRABAJO PRINCIPALES

### Flujo Completo: Desde Registro hasta Consulta

```
1. REGISTRO DE CLIENTE
   ├── Cliente se registra en Auth0
   ├── Frontend llama a POST /api/users/register-from-auth0
   ├── Sistema crea usuario con ROLE_CLIENTE
   └── Cliente puede acceder al sistema

2. REGISTRO DE MASCOTA
   ├── Veterinario o cliente accede al formulario
   ├── POST /api/mascotas con datos completos
   ├── Sistema valida cliente y raza
   ├── Crea mascota con estado activo
   └── Sistema automáticamente crea historial clínico

3. AGENDAMIENTO DE CITA
   ├── Usuario consulta disponibilidad
   ├── GET /api/citas/disponibilidad
   ├── Selecciona fecha y hora disponible
   ├── POST /api/citas con todos los datos
   ├── Sistema valida:
   │   ├── Horario de sucursal
   │   ├── Disponibilidad de veterinario
   │   └── Estado activo de cliente y mascota
   ├── Crea cita con estado PENDIENTE
   ├── Envía notificación por email
   └── Envía notificación por WebSocket

4. CONFIRMACIÓN DE CITA
   ├── Cliente recibe notificación
   ├── PUT /api/citas/{id} con estado CONFIRMADA
   └── Sistema programa recordatorio

5. ATENCIÓN DE CITA
   ├── Veterinario accede a cita del día
   ├── Revisa historial de mascota
   ├── POST /api/consultas con diagnóstico
   ├── Si requiere: POST /api/tratamientos
   ├── Si requiere: POST /api/vacunacion-mascota
   └── PUT /api/citas/{id} con estado ATENDIDA

6. SEGUIMIENTO
   ├── Cliente recibe notificaciones de seguimiento
   ├── Puede consultar tratamientos activos
   ├── Recibe recordatorios de próximas dosis
   └── Sistema genera alertas automáticas
```

### Flujo de Vacunación

```
1. CONFIGURACIÓN DE VACUNA
   ├── Administrador crea vacuna
   ├── POST /api/vacunas
   └── Define especies aplicables

2. CREACIÓN DE ESQUEMA
   ├── POST /api/esquemas-vacunacion
   ├── Define dosis y edades
   └── Agrega observaciones

3. ASIGNACIÓN A MASCOTA
   ├── Veterinario evalúa mascota
   ├── POST /api/mascota-esquemas
   ├── Asigna esquemas apropiados
   └── Estado inicial: PENDIENTE

4. APLICACIÓN DE VACUNA
   ├── Durante consulta veterinaria
   ├── POST /api/vacunacion-mascota
   ├── Registra fecha de aplicación
   ├── Calcula próxima dosis
   └── Actualiza estado a COMPLETADO

5. SEGUIMIENTO
   ├── Sistema genera recordatorios
   ├── Notifica fecha de próxima dosis
   └── Mantiene registro completo
```

---

## CONSIDERACIONES IMPORTANTES

### Seguridad

- Todos los endpoints requieren autenticación excepto registro
- Validación de roles en cada endpoint
- Tokens JWT con expiración
- Contraseñas encriptadas

### Validación de Datos

- Validación en DTOs con Jakarta Bean Validation
- Validación de reglas de negocio en servicios
- Manejo de errores consistente

### Rendimiento

- Lazy loading en relaciones de entidades
- Paginación en listas grandes
- Índices en campos de búsqueda frecuente

### Mantenibilidad

- Código organizado en capas
- Documentación con Swagger
- Convenciones de nomenclatura consistentes
- Manejo centralizado de excepciones

### Escalabilidad

- Arquitectura stateless con JWT
- Base de datos relacional normalizada
- Posibilidad de microservicios futuros
- WebSocket para comunicación en tiempo real

---

Este documento proporciona una guía completa de las funcionalidades del sistema Huellitasoft Backend. Para información técnica sobre arquitectura y configuración, consultar el documento DOCUMENTACION_TECNICA.md.
