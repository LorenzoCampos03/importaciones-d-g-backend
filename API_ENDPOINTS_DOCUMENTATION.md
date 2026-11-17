# 🚀 API Importaciones Backend - Documentación de Endpoints

## 📋 Tabla de Contenidos

- [Información General](#información-general)
- [Autenticación y Autorización](#autenticación-y-autorización)
- [🗺️ MAPA COMPLETO DE ENDPOINTS](#🗺️-mapa-completo-de-endpoints)
- [Endpoints de Autenticación](#endpoints-de-autenticación)
- [Endpoints de Clientes](#endpoints-de-clientes)
- [Endpoints de Empleados/Vendedores](#endpoints-de-empleadosvendedores)
- [Endpoints de Productos](#endpoints-de-productos)
- [Endpoints de Ventas](#endpoints-de-ventas)
- [Endpoints de Compras](#endpoints-de-compras)
- [Endpoints de Oficinas](#endpoints-de-oficinas)
- [Endpoints de Reportes](#endpoints-de-reportes)
- [Endpoints de Catálogos](#endpoints-de-catálogos)
- [Endpoints de Detalles de Venta](#endpoints-de-detalles-de-venta)
- [Endpoints de Administración](#endpoints-de-administración)
- [Códigos de Respuesta](#códigos-de-respuesta)
- [Ejemplos de Uso](#ejemplos-de-uso)

---

## 🔧 Información General

**Base URL:** `http://localhost:8080`
**Versión:** v1
**Framework:** Spring Boot 3.4.4
**Seguridad:** JWT + Spring Security + BCrypt

### Roles Disponibles

- **Cliente**: Usuario final con acceso limitado
- **Vendedor**: Empleado con permisos de gestión de clientes
- **Administrador**: Acceso completo al sistema

---

## 🔐 Autenticación y Autorización

### Autenticación JWT

Todos los endpoints protegidos requieren un token JWT en el header:

```
Authorization: Bearer <tu_jwt_token>
```

### Niveles de Acceso

| Endpoint | Cliente | Vendedor | Administrador |
|----------|---------|----------|---------------|
| Ver clientes | ❌ | ✅ | ✅ |
| Crear clientes | ❌ | ✅ | ✅ |
| Gestionar vendedores | ❌ | ❌ | ✅ |
| Eliminar usuarios | ❌ | ❌ | ✅ |

---

## �️ MAPA COMPLETO DE ENDPOINTS

### 📊 Resumen por Módulos

| **Módulo** | **Total Endpoints** | **Público** | **Cliente** | **Vendedor** | **Administrador** |
|------------|-------------------|------------|------------|-------------|------------------|
| **Autenticación** | 8 | 8 | - | - | - |
| **Clientes** | 10 | - | 1 | 8 | 10 |
| **Empleados** | 9 | - | - | 1 | 9 |
| **Productos** | 8 | - | 2 | 8 | 8 |
| **Ventas** | 12 | 2 | 8 | 10 | 10 |
| **Compras** | 6 | 6 | 6 | 6 | 6 |
| **Oficinas** | 9 | - | - | 9 | 9 |
| **Reportes** | 3 | - | 3 | 3 | 3 |
| **Catálogos** | 3 | - | - | 3 | 3 |
| **Detalles de Venta** | 6 | 6 | 6 | 6 | 6 |
| **Administración** | 2 | 1 | - | - | 2 |
| **TOTAL** | **76** | **23** | **26** | **58** | **66** |

### 🔐 Tabla Detallada de Permisos por Endpoint


#### 🔑 **AUTENTICACIÓN** (`/api/v1/auth`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| POST | `/login` | ✅ | ✅ | ✅ | ✅ | Iniciar sesión |
| POST | `/register` | ✅ | ✅ | ✅ | ✅ | Registrar usuario |
| PUT | `/change-password/{userId}` | ✅ | ✅ | ✅ | ✅ | Cambiar contraseña |
| POST | `/refresh` | ✅ | ✅ | ✅ | ✅ | Renovar token |
| POST | `/logout` | ✅ | ✅ | ✅ | ✅ | Cerrar sesión |
| GET | `/validate/username/{username}` | ✅ | ✅ | ✅ | ✅ | Validar username |
| GET | `/validate/email/{email}` | ✅ | ✅ | ✅ | ✅ | Validar email |
| GET | `/validate/document` | ✅ | ✅ | ✅ | ✅ | Validar documento |
| GET | `/validate/phone/{phone}` | ✅ | ✅ | ✅ | ✅ | Validar teléfono |

#### 👥 **CLIENTES** (`/api/v1/client`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/active` | ❌ | ❌ | ✅ | ✅ | Listar clientes activos |
| GET | `/all` | ❌ | ❌ | ❌ | ✅ | Listar todos los clientes |
| GET | `/status/{status}` | ❌ | ❌ | ✅ | ✅ | Clientes por estado |
| GET | `/{id}` | ❌ | ✅ | ✅ | ✅ | Obtener cliente por ID |
| POST | `/create` | ❌ | ❌ | ✅ | ✅ | Crear nuevo cliente |
| PUT | `/update` | ❌ | ❌ | ✅ | ✅ | Actualizar cliente |
| PATCH | `/partialUpdate/{id}` | ❌ | ❌ | ✅ | ✅ | Actualización parcial |
| PUT | `/logicalDelete/{id}` | ❌ | ❌ | ❌ | ✅ | Desactivar cliente |
| PATCH | `/restore/{id}` | ❌ | ❌ | ❌ | ✅ | Restaurar cliente |
| DELETE | `/permanent/{id}` | ❌ | ❌ | ❌ | ✅ | Eliminar permanente |

#### 💼 **EMPLEADOS** (`/api/v1/employee`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/listEmployee` | ❌ | ❌ | ❌ | ✅ | Listar empleados activos |
| GET | `/all` | ❌ | ❌ | ❌ | ✅ | Listar todos los empleados |
| GET | `/{id}` | ❌ | ❌ | ✅ | ✅ | Obtener empleado por ID |
| POST | `/saveEmployee` | ❌ | ❌ | ❌ | ✅ | Crear nuevo empleado |
| PUT | `/update` | ❌ | ❌ | ❌ | ✅ | Actualizar empleado |
| PATCH | `/partialUpdate/{id}` | ❌ | ❌ | ❌ | ✅ | Actualización parcial |
| PUT | `/logicalDelete/{id}` | ❌ | ❌ | ❌ | ✅ | Desactivar empleado |
| PATCH | `/restore/{id}` | ❌ | ❌ | ❌ | ✅ | Restaurar empleado |
| DELETE | `/permanent/{id}` | ❌ | ❌ | ❌ | ✅ | Eliminar permanente |

#### 📦 **PRODUCTOS** (`/api/v1/products`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/` | ❌ | ❌ | ✅ | ✅ | Listar todos los productos |
| GET | `/{productId}` | ❌ | ❌ | ✅ | ✅ | Obtener producto por ID |
| GET | `/status/{status}` | ❌ | ❌ | ✅ | ✅ | Productos por estado |
| POST | `/` | ❌ | ❌ | ✅ | ✅ | Crear nuevo producto |
| PUT | `/{id}` | ❌ | ❌ | ✅ | ✅ | Actualizar producto |
| PUT | `/delete/{id}` | ❌ | ❌ | ✅ | ✅ | Eliminar lógicamente |
| PUT | `/restore/{id}` | ❌ | ❌ | ✅ | ✅ | Restaurar producto |
| GET | `/available` | ❌ | ✅ | ✅ | ✅ | Productos disponibles |
| GET | `/available/{productId}` | ❌ | ✅ | ✅ | ✅ | Producto disponible por ID |

#### 🛒 **VENTAS** (`/api/v1/sale`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/` | ❌ | ❌ | ✅ | ✅ | Todas las ventas |
| GET | `/{id}` | ❌ | ✅ | ✅ | ✅ | Venta por ID |
| POST | `/` | ❌ | ❌ | ✅ | ✅ | Crear nueva venta |
| DELETE | `/{id}` | ✅ | ✅ | ✅ | ✅ | Eliminar venta |
| PUT | `/{id}/restore` | ❌ | ❌ | ✅ | ✅ | Restaurar venta |
| PUT | `/{id}/status` | ❌ | ✅ | ✅ | ✅ | Actualizar estado |
| DELETE | `/{saleId}/details/{detailId}` | ❌ | ✅ | ✅ | ✅ | Remover detalle |
| POST | `/{saleId}/details` | ❌ | ✅ | ✅ | ✅ | Agregar detalle |
| PUT | `/{saleId}/details/{detailId}` | ❌ | ✅ | ✅ | ✅ | Actualizar cantidad |
| GET | `/status/{status}` | ❌ | ✅ | ✅ | ✅ | Ventas por estado |
| GET | `/active` | ✅ | ✅ | ✅ | ✅ | Ventas activas |
| GET | `/client/{clientId}` | ✅ | ✅ | ✅ | ✅ | Ventas por cliente |

#### 🛍️ **COMPRAS** (`/api/v1/purchases`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/` | ✅ | ✅ | ✅ | ✅ | Todas las compras |
| GET | `/{id}` | ✅ | ✅ | ✅ | ✅ | Compra por ID |
| POST | `/` | ✅ | ✅ | ✅ | ✅ | Crear nueva compra |
| PUT | `/{id}` | ✅ | ✅ | ✅ | ✅ | Actualizar compra |
| DELETE | `/{id}` | ✅ | ✅ | ✅ | ✅ | Cancelar compra |
| GET | `/status/{status}` | ✅ | ✅ | ✅ | ✅ | Compras por estado |

#### 🏢 **OFICINAS** (`/api/v1/office`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/active` | ❌ | ❌ | ✅ | ✅ | Oficinas activas |
| GET | `/all` | ❌ | ❌ | ✅ | ✅ | Todas las oficinas |
| GET | `/status/{status}` | ❌ | ❌ | ✅ | ✅ | Oficinas por estado |
| GET | `/{id}` | ❌ | ❌ | ✅ | ✅ | Oficina por ID |
| POST | `/create` | ❌ | ❌ | ✅ | ✅ | Crear oficina |
| PUT | `/update` | ❌ | ❌ | ✅ | ✅ | Actualizar oficina |
| PUT | `/logicalDelete/{id}` | ❌ | ❌ | ✅ | ✅ | Desactivar oficina |
| PATCH | `/restore/{id}` | ❌ | ❌ | ✅ | ✅ | Restaurar oficina |
| DELETE | `/permanent/{id}` | ❌ | ❌ | ✅ | ✅ | Eliminar permanente |

#### 📊 **REPORTES** (`/api/v1/reports`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/sale/{sale_id}` | ❌ | ✅ | ✅ | ✅ | Reporte de venta |
| GET | `/product` | ❌ | ✅ | ✅ | ✅ | Reporte de productos |
| GET | `/purchase/{purchase_id}` | ❌ | ✅ | ✅ | ✅ | Reporte de compra |

#### 📚 **CATÁLOGOS** (`/api/v1`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/categories` | ❌ | ❌ | ✅ | ✅ | Listar categorías |
| GET | `/suppliers` | ❌ | ❌ | ✅ | ✅ | Listar proveedores |
| GET | `/brands` | ❌ | ❌ | ✅ | ✅ | Listar marcas |

#### 🧾 **DETALLES DE VENTA** (`/api/v1/sale-details`)
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| GET | `/` | ✅ | ✅ | ✅ | ✅ | Todos los detalles |
| GET | `/sale/{saleId}` | ✅ | ✅ | ✅ | ✅ | Detalles por venta |
| GET | `/{id}` | ✅ | ✅ | ✅ | ✅ | Detalle por ID |
| POST | `/sale/{saleId}` | ✅ | ✅ | ✅ | ✅ | Crear detalle |
| PUT | `/{id}` | ✅ | ✅ | ✅ | ✅ | Actualizar detalle |
| DELETE | `/{id}` | ✅ | ✅ | ✅ | ✅ | Eliminar detalle |

#### ⚙️ **ADMINISTRACIÓN**
| **Endpoint** | **Método** | **Ruta** | **Público** | **Cliente** | **Vendedor** | **Admin** | **Descripción** |
|-------------|-----------|----------|------------|------------|-------------|-----------|----------------|
| POST | `/api/v1/admin/migrate-passwords` | ❌ | ❌ | ❌ | ✅ | Migrar contraseñas |
| POST | `/api/v1/migration/migrate-passwords` | ✅ | ✅ | ✅ | ✅ | Migrar contraseñas (público) |

### 📈 Estadísticas de Acceso

- **Total de endpoints únicos**: 76
- **Endpoints públicos**: 23 (30.3%)
- **Endpoints que requieren autenticación**: 53 (69.7%)
- **Endpoints accesibles por Cliente**: 26 (34.2%)
- **Endpoints accesibles por Vendedor**: 58 (76.3%)
- **Endpoints accesibles por Administrador**: 66 (86.8%)

### 🚨 Endpoints Sin Protección de Seguridad

Los siguientes endpoints **NO tienen anotaciones `@PreAuthorize`** y están accesibles públicamente:

1. **Compras** (`/api/v1/purchases`) - Todos los endpoints (6 endpoints)
2. **Detalles de Venta** (`/api/v1/sale-details`) - Todos los endpoints (6 endpoints)
3. **Ventas** - Algunos endpoints:
   - `DELETE /{id}` (eliminar venta)
   - `GET /active` (ventas activas)
   - `GET /client/{clientId}` (ventas por cliente)
4. **Migración** (`/api/v1/migration/migrate-passwords`) - 1 endpoint

> ⚠️ **RECOMENDACIÓN**: Revisar estos endpoints y agregar las anotaciones de seguridad apropiadas según el nivel de acceso requerido.

---

## �🔑 Endpoints de Autenticación

### 1. Login

**POST** `/api/v1/auth/login`

- **Descripción**: Autentica un usuario y devuelve JWT
- **Acceso**: Público
- **Body**:

```json
{
  "username": "string",
  "password": "string"
}
```

- **Respuesta exitosa (200)**:

```json
{
  "success": true,
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "usuario",
  "role": "Cliente",
  "name": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com"
}
```

### 2. Registro

**POST** `/api/v1/auth/register`

- **Descripción**: Registra un nuevo usuario
- **Acceso**: Público
- **Body**:

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "last_name": "string",
  "email": "string",
  "phone": "string",
  "address": "string",
  "document_type": "DNI",
  "document_number": "string"
}
```

### 3. Refresh Token ⭐ NUEVO

**POST** `/api/v1/auth/refresh`

- **Descripción**: Renueva el access token usando el refresh token
- **Acceso**: Público
- **Request Body**:

```json
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

- **Response (200 OK)**:

```json
{
    "success": true,
    "message": "Token renovado exitosamente",
    "token": "eyJhbGciOiJIUzI1NiJ9...", // Nuevo Access Token
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...", // Nuevo Refresh Token
    "userId": 1,
    "username": "admin",
    "role": "Administrador",
    "name": "Juan",
    "lastName": "Pérez",
    "email": "admin@ejemplo.com"
}
```

- **Response (401 Unauthorized)**:

```json
{
    "success": false,
    "message": "Refresh token inválido"
}
```

### 4. Logout ⭐ NUEVO

**POST** `/api/v1/auth/logout?username={username}`

- **Descripción**: Invalida el refresh token del usuario
- **Acceso**: Público
- **Query Parameters**:
  - `username`: string - Nombre de usuario

- **Response (200 OK)**:

```json
{
    "message": "Logout exitoso"
}
```

---

## 👥 Endpoints de Clientes

### 1. Listar Clientes Activos

**GET** `/api/v1/client/active`

- **Descripción**: Lista todos los clientes con estatus activo
- **Acceso**: Vendedor, Administrador
- **Headers**: `Authorization: Bearer <token>`

### 2. Listar Todos los Clientes

**GET** `/api/v1/client/all`

- **Descripción**: Lista todos los clientes (activos e inactivos)
- **Acceso**: Solo Administrador
- **Headers**: `Authorization: Bearer <token>`

### 3. Listar Clientes por Estado

**GET** `/api/v1/client/status/{status}`

- **Descripción**: Lista clientes filtrados por estado (A/I)
- **Acceso**: Vendedor, Administrador
- **Parámetros**:
  - `status`: A (Activo) o I (Inactivo)

### 4. Obtener Cliente por ID

**GET** `/api/v1/client/{id}`

- **Descripción**: Obtiene un cliente específico por ID
- **Acceso**: Cliente (solo su perfil), Vendedor, Administrador
- **Parámetros**:
  - `id`: ID del cliente

### 5. Crear Cliente

**POST** `/api/v1/client/create`

- **Descripción**: Crea un nuevo cliente
- **Acceso**: Vendedor, Administrador
- **Body**:

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "last_name": "string",
  "email": "string",
  "phone": "string",
  "address": "string",
  "document_type": "DNI",
  "document_number": "string",
  "razon_social": "string (opcional)"
}
```

### 6. Actualizar Cliente

**PUT** `/api/v1/client/update`

- **Descripción**: Actualiza completamente un cliente
- **Acceso**: Vendedor, Administrador
- **Body**: Objeto cliente completo con `user_id`

### 7. Actualización Parcial de Cliente

**PATCH** `/api/v1/client/partialUpdate/{id}`

- **Descripción**: Actualiza campos específicos de un cliente
- **Acceso**: Vendedor, Administrador
- **Body**: Objeto con solo los campos a actualizar

### 8. Desactivar Cliente (Eliminación Lógica)

**PUT** `/api/v1/client/logicalDelete/{id}`

- **Descripción**: Cambia el estado del cliente a inactivo
- **Acceso**: Solo Administrador

### 9. Restaurar Cliente

**PATCH** `/api/v1/client/restore/{id}`

- **Descripción**: Reactiva un cliente desactivado
- **Acceso**: Solo Administrador

### 10. Eliminar Cliente Permanentemente

**DELETE** `/api/v1/client/permanent/{id}`

- **Descripción**: Elimina físicamente un cliente de la BD
- **Acceso**: Solo Administrador
- **⚠️ CUIDADO**: Esta acción es irreversible

---

## 💼 Endpoints de Empleados/Vendedores

### 1. Listar Vendedores Activos

**GET** `/api/v1/employee/listEmployee`

- **Descripción**: Lista todos los vendedores activos
- **Acceso**: Solo Administrador

### 2. Listar Todos los Vendedores

**GET** `/api/v1/employee/all`

- **Descripción**: Lista todos los vendedores (activos e inactivos)
- **Acceso**: Solo Administrador

### 3. Obtener Vendedor por ID

**GET** `/api/v1/employee/{id}`

- **Descripción**: Obtiene un vendedor específico
- **Acceso**: Administrador, Vendedor (puede ver su perfil)

### 4. Crear Vendedor

**POST** `/api/v1/employee/saveEmployee`

- **Descripción**: Crea un nuevo vendedor
- **Acceso**: Solo Administrador
- **Body**:

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "last_name": "string",
  "email": "string",
  "phone": "string",
  "address": "string",
  "document_type": "DNI",
  "document_number": "string",
  "hire_date": "2025-06-22",
  "office_id": 1
}
```

### 5. Actualizar Vendedor

**PUT** `/api/v1/employee/update`

- **Descripción**: Actualiza completamente un vendedor
- **Acceso**: Solo Administrador

### 6. Actualización Parcial de Vendedor

**PATCH** `/api/v1/employee/partialUpdate/{id}`

- **Descripción**: Actualiza campos específicos de un vendedor
- **Acceso**: Solo Administrador

### 7. Desactivar Vendedor

**PUT** `/api/v1/employee/logicalDelete/{id}`

- **Descripción**: Cambia el estado del vendedor a inactivo
- **Acceso**: Solo Administrador

### 8. Restaurar Vendedor

**PATCH** `/api/v1/employee/restore/{id}`

- **Descripción**: Reactiva un vendedor desactivado
- **Acceso**: Solo Administrador

### 9. Eliminar Vendedor Permanentemente

**DELETE** `/api/v1/employee/permanent/{id}`

- **Descripción**: Elimina físicamente un vendedor
- **Acceso**: Solo Administrador

---

## 📦 Endpoints de Productos

### 1. Listar Todos los Productos

**GET** `/api/v1/products`

- **Descripción**: Lista todos los productos en el sistema
- **Acceso**: Vendedor, Administrador
- **Headers**: `Authorization: Bearer <token>`

### 2. Obtener Producto por ID

**GET** `/api/v1/products/{productId}`

- **Descripción**: Obtiene un producto específico por ID
- **Acceso**: Vendedor, Administrador
- **Parámetros**:
  - `productId`: ID del producto

### 3. Listar Productos por Estado

**GET** `/api/v1/products/status/{status}`

- **Descripción**: Lista productos filtrados por estado (A/I)
- **Acceso**: Vendedor, Administrador
- **Parámetros**:
  - `status`: A (Activo) o I (Inactivo)

### 4. Crear Producto

**POST** `/api/v1/products`

- **Descripción**: Crea un nuevo producto
- **Acceso**: Vendedor, Administrador
- **Body**: Objeto ProductDTO

### 5. Actualizar Producto

**PUT** `/api/v1/products/{id}`

- **Descripción**: Actualiza un producto existente
- **Acceso**: Vendedor, Administrador

### 6. Eliminar Producto (Lógico)

**PUT** `/api/v1/products/delete/{id}`

- **Descripción**: Elimina lógicamente un producto
- **Acceso**: Vendedor, Administrador

### 7. Restaurar Producto

**PUT** `/api/v1/products/restore/{id}`

- **Descripción**: Restaura un producto eliminado lógicamente
- **Acceso**: Vendedor, Administrador

### 8. Productos Disponibles para Clientes

**GET** `/api/v1/products/available`

- **Descripción**: Lista productos disponibles (activos y con stock)
- **Acceso**: Cliente, Vendedor, Administrador

**GET** `/api/v1/products/available/{productId}`

- **Descripción**: Obtiene un producto disponible específico
- **Acceso**: Cliente, Vendedor, Administrador

---

## 🛒 Endpoints de Ventas

### 1. Listar Todas las Ventas

**GET** `/api/v1/sale`

- **Descripción**: Lista todas las ventas del sistema
- **Acceso**: Vendedor, Administrador

### 2. Obtener Venta por ID

**GET** `/api/v1/sale/{id}`

- **Descripción**: Obtiene una venta específica por ID
- **Acceso**: Cliente, Vendedor, Administrador

### 3. Crear Nueva Venta

**POST** `/api/v1/sale`

- **Descripción**: Crea una nueva venta
- **Acceso**: Vendedor, Administrador
- **Body**: Objeto SaleRequestDTO

### 4. Eliminar Venta (Lógico)

**DELETE** `/api/v1/sale/{id}`

- **Descripción**: Elimina lógicamente una venta
- **Acceso**: ⚠️ Público (sin restricciones)

### 5. Restaurar Venta

**PUT** `/api/v1/sale/{id}/restore`

- **Descripción**: Restaura una venta eliminada
- **Acceso**: Vendedor, Administrador

### 6. Actualizar Estado de Venta

**PUT** `/api/v1/sale/{id}/status`

- **Descripción**: Actualiza el estado de una venta
- **Acceso**: Cliente, Vendedor, Administrador

### 7. Gestión de Detalles de Venta

**POST** `/api/v1/sale/{saleId}/details`

- **Descripción**: Agrega un detalle a la venta
- **Acceso**: Cliente, Vendedor, Administrador

**PUT** `/api/v1/sale/{saleId}/details/{detailId}`

- **Descripción**: Actualiza cantidad de un detalle
- **Acceso**: Cliente, Vendedor, Administrador

**DELETE** `/api/v1/sale/{saleId}/details/{detailId}`

- **Descripción**: Elimina un detalle de la venta
- **Acceso**: Cliente, Vendedor, Administrador

### 8. Consultas Especiales

**GET** `/api/v1/sale/status/{status}`

- **Descripción**: Ventas filtradas por estado
- **Acceso**: Cliente, Vendedor, Administrador

**GET** `/api/v1/sale/active`

- **Descripción**: Lista ventas activas
- **Acceso**: ⚠️ Público (sin restricciones)

**GET** `/api/v1/sale/client/{clientId}`

- **Descripción**: Ventas de un cliente específico
- **Acceso**: ⚠️ Público (sin restricciones)

---

## 🛍️ Endpoints de Compras

### 1. Listar Todas las Compras

**GET** `/api/v1/purchases`

- **Descripción**: Lista todas las compras
- **Acceso**: ⚠️ Público (sin restricciones)

### 2. Obtener Compra por ID

**GET** `/api/v1/purchases/{id}`

- **Descripción**: Obtiene una compra específica
- **Acceso**: ⚠️ Público (sin restricciones)

### 3. Crear Nueva Compra

**POST** `/api/v1/purchases`

- **Descripción**: Crea una nueva compra con detalles
- **Acceso**: ⚠️ Público (sin restricciones)

### 4. Actualizar Compra

**PUT** `/api/v1/purchases/{id}`

- **Descripción**: Actualiza una compra existente
- **Acceso**: ⚠️ Público (sin restricciones)

### 5. Cancelar Compra

**DELETE** `/api/v1/purchases/{id}`

- **Descripción**: Cancela una compra
- **Acceso**: ⚠️ Público (sin restricciones)

### 6. Compras por Estado

**GET** `/api/v1/purchases/status/{status}`

- **Descripción**: Lista compras filtradas por estado
- **Acceso**: ⚠️ Público (sin restricciones)

---

## 🏢 Endpoints de Oficinas

### 1. Listar Oficinas Activas

**GET** `/api/v1/office/active`

- **Descripción**: Lista todas las oficinas activas
- **Acceso**: Vendedor, Administrador

### 2. Listar Todas las Oficinas

**GET** `/api/v1/office/all`

- **Descripción**: Lista todas las oficinas (activas e inactivas)
- **Acceso**: Vendedor, Administrador

### 3. Oficinas por Estado

**GET** `/api/v1/office/status/{status}`

- **Descripción**: Lista oficinas filtradas por estado
- **Acceso**: Vendedor, Administrador

### 4. Obtener Oficina por ID

**GET** `/api/v1/office/{id}`

- **Descripción**: Obtiene una oficina específica
- **Acceso**: Vendedor, Administrador

### 5. Crear Oficina

**POST** `/api/v1/office/create`

- **Descripción**: Crea una nueva oficina
- **Acceso**: Vendedor, Administrador

### 6. Actualizar Oficina

**PUT** `/api/v1/office/update`

- **Descripción**: Actualiza una oficina existente
- **Acceso**: Vendedor, Administrador

### 7. Desactivar Oficina

**PUT** `/api/v1/office/logicalDelete/{id}`

- **Descripción**: Desactiva lógicamente una oficina
- **Acceso**: Vendedor, Administrador

### 8. Restaurar Oficina

**PATCH** `/api/v1/office/restore/{id}`

- **Descripción**: Restaura una oficina desactivada
- **Acceso**: Vendedor, Administrador

### 9. Eliminar Oficina Permanentemente

**DELETE** `/api/v1/office/permanent/{id}`

- **Descripción**: Elimina físicamente una oficina
- **Acceso**: Vendedor, Administrador

---

## 📊 Endpoints de Reportes

### 1. Reporte de Venta

**GET** `/api/v1/reports/sale/{sale_id}`

- **Descripción**: Genera reporte PDF de una venta específica
- **Acceso**: Cliente, Vendedor, Administrador
- **Response**: Archivo PDF

### 2. Reporte de Productos

**GET** `/api/v1/reports/product`

- **Descripción**: Genera reporte PDF de todos los productos
- **Acceso**: Cliente, Vendedor, Administrador
- **Response**: Archivo PDF

### 3. Reporte de Compra

**GET** `/api/v1/reports/purchase/{purchase_id}`

- **Descripción**: Genera reporte PDF de una compra específica
- **Acceso**: Cliente, Vendedor, Administrador
- **Response**: Archivo PDF

---

## 📚 Endpoints de Catálogos

### 1. Listar Categorías

**GET** `/api/v1/categories`

- **Descripción**: Lista todas las categorías de productos
- **Acceso**: Vendedor, Administrador

### 2. Listar Proveedores

**GET** `/api/v1/suppliers`

- **Descripción**: Lista todos los proveedores
- **Acceso**: Vendedor, Administrador

### 3. Listar Marcas

**GET** `/api/v1/brands`

- **Descripción**: Lista todas las marcas de productos
- **Acceso**: Vendedor, Administrador

---

## 🧾 Endpoints de Detalles de Venta

### 1. Listar Todos los Detalles

**GET** `/api/v1/sale-details`

- **Descripción**: Lista todos los detalles de venta
- **Acceso**: ⚠️ Público (sin restricciones)

### 2. Detalles por Venta

**GET** `/api/v1/sale-details/sale/{saleId}`

- **Descripción**: Lista detalles de una venta específica
- **Acceso**: ⚠️ Público (sin restricciones)

### 3. Obtener Detalle por ID

**GET** `/api/v1/sale-details/{id}`

- **Descripción**: Obtiene un detalle específico por ID
- **Acceso**: ⚠️ Público (sin restricciones)

### 4. Crear Detalle de Venta

**POST** `/api/v1/sale-details/sale/{saleId}`

- **Descripción**: Crea un nuevo detalle de venta
- **Acceso**: ⚠️ Público (sin restricciones)

### 5. Actualizar Detalle

**PUT** `/api/v1/sale-details/{id}`

- **Descripción**: Actualiza un detalle de venta
- **Acceso**: ⚠️ Público (sin restricciones)

### 6. Eliminar Detalle

**DELETE** `/api/v1/sale-details/{id}`

- **Descripción**: Elimina físicamente un detalle de venta
- **Acceso**: ⚠️ Público (sin restricciones)

---

## ⚙️ Endpoints de Administración

### 1. Migrar Contraseñas (Administrador)

**POST** `/api/v1/admin/migrate-passwords`

- **Descripción**: Migra contraseñas de texto plano a BCrypt
- **Acceso**: Solo Administrador
- **⚠️ Nota**: Usar solo una vez después de implementar seguridad

### 2. Migrar Contraseñas (Público)

**POST** `/api/v1/migration/migrate-passwords`

- **Descripción**: Migra contraseñas de texto plano a BCrypt
- **Acceso**: ⚠️ Público (sin restricciones)
- **⚠️ RIESGO DE SEGURIDAD**: Este endpoint está expuesto públicamente

---

## 📊 Códigos de Respuesta

| Código | Descripción |
|--------|-------------|
| 200 | ✅ Operación exitosa |
| 201 | ✅ Recurso creado exitosamente |
| 204 | ✅ Operación exitosa sin contenido |
| 400 | ❌ Solicitud inválida |
| 401 | ❌ No autenticado |
| 403 | ❌ Acceso denegado (sin permisos) |
| 404 | ❌ Recurso no encontrado |
| 500 | ❌ Error interno del servidor |

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Login y uso del token

```bash
# 1. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_user",
    "password": "password123"
  }'

# Respuesta:
# {
#   "success": true,
#   "token": "eyJhbGciOiJIUzI1NiJ9...",
#   "role": "Administrador"
# }

# 2. Usar el token para listar clientes
curl -X GET http://localhost:8080/api/v1/client/active \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Ejemplo 2: Crear un nuevo cliente

```bash
curl -X POST http://localhost:8080/api/v1/client/create \
  -H "Authorization: Bearer <tu_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo_cliente",
    "password": "password123",
    "name": "María",
    "last_name": "García",
    "email": "maria@email.com",
    "phone": "987654321",
    "address": "Av. Principal 123",
    "document_type": "DNI",
    "document_number": "12345678"
  }'
```

### Ejemplo 3: Actualización parcial

```bash
curl -X PATCH http://localhost:8080/api/v1/client/partialUpdate/1 \
  -H "Authorization: Bearer <tu_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "999888777",
    "address": "Nueva dirección 456"
  }'
```
