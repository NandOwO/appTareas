<div align="center">

# 📋 Synapse - Sistema de Gestión de Tareas

<p align="center">
  <img src="https://img.shields.io/badge/Java-11+-orange?style=for-the-badge&logo=java" alt="Java 11+"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker" alt="Docker"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License"/>
</p>

<p align="center">
  <strong>Sistema empresarial completo para la gestión de tareas, equipos y asignaciones con notificaciones automáticas por email</strong>
</p>

</div>

---

## 📖 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características Principales](#-características-principales)
- [Tecnologías](#️-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Inicio Rápido](#-inicio-rápido)
- [Uso de la Aplicación](#-uso-de-la-aplicación)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Configuración Avanzada](#-configuración-avanzada)
- [Solución de Problemas](#-solución-de-problemas)
- [Documentación](#-documentación)

---

## 🎯 Descripción

**Synapse** es un sistema de gestión de tareas empresarial diseñado para facilitar la organización, asignación y seguimiento de tareas en equipos de trabajo. La aplicación permite a los gerentes crear y asignar tareas a empleados individuales o equipos completos, con notificaciones automáticas por email y soporte para adjuntos de archivos.

### ¿Para quién es este sistema?

- **Empresas** que necesitan organizar tareas entre múltiples equipos
- **Gerentes** que requieren asignar y dar seguimiento a tareas
- **Empleados** que necesitan gestionar sus tareas asignadas
- **Administradores** que gestionan usuarios y equipos

---

## ✨ Características Principales

### 🔐 Sistema de Roles y Permisos

<table>
<tr>
<td width="33%">

#### 👨‍💼 Administrador
- Gestión completa de usuarios
- Crear, editar y eliminar usuarios
- Asignar roles y permisos
- Gestión de equipos
- Acceso a todos los módulos

</td>
<td width="33%">

#### 👔 Gerente
- Crear y asignar tareas
- Gestionar equipos propios
- Asignar tareas a empleados o equipos
- Ver dashboard de tareas
- Adjuntar archivos a tareas
- Recibir notificaciones de tareas completadas

</td>
<td width="33%">

#### 👨‍💻 Empleado
- Ver tareas asignadas
- Actualizar estado de tareas
- Ver detalles de tareas
- Recibir notificaciones por email
- Gestionar perfil propio

</td>
</tr>
</table>

### 📊 Gestión de Tareas

- ✅ **Crear tareas** con título, descripción, fecha límite y prioridad
- ✅ **Asignación flexible**: Individual o a equipos completos
- ✅ **Adjuntar archivos**: Imágenes y documentos
- ✅ **Estados**: Pendiente, En Progreso, Completada, Pausada
- ✅ **Prioridades**: Baja, Media, Alta
- ✅ **Edición completa**: Modificar todos los campos de una tarea
- ✅ **Archivar tareas**: Soft delete para mantener historial
- ✅ **Visualización detallada**: Ver toda la información de una tarea

### 👥 Gestión de Equipos

- ✅ **Crear equipos** con nombre y descripción
- ✅ **Asignar miembros** de forma visual
- ✅ **Líder de equipo**: Cada equipo tiene un gerente asignado
- ✅ **Editar equipos**: Modificar nombre, descripción y miembros
- ✅ **Ver miembros**: Lista completa de integrantes
- ✅ **Eliminar equipos**: Con confirmación de seguridad

### 📧 Notificaciones Automáticas

- ✅ **Email al asignar tarea**: Notificación automática con detalles completos
- ✅ **Asignación individual**: Email personalizado al empleado
- ✅ **Asignación a equipo**: Email a todos los miembros del equipo
- ✅ **Tarea completada**: Notificación al gerente cuando un empleado completa una tarea
- ✅ **Templates HTML profesionales**: Emails con diseño moderno y responsive

### 🎨 Interfaz de Usuario

- ✅ **Diseño moderno**: Interfaz limpia y profesional con FlatLaf
- ✅ **Componentes personalizados**: Pills, toggles, renderers personalizados
- ✅ **Iconos intuitivos**: Navegación clara con iconos SVG
- ✅ **Notificaciones toast**: Feedback visual inmediato
- ✅ **Validaciones en tiempo real**: Formularios con validación de datos

---

## 🛠️ Tecnologías

### Backend
- **Java 11+**: Lenguaje principal
- **JDBC**: Conexión a base de datos
- **BCrypt**: Encriptación segura de contraseñas
- **JavaMail**: Envío de emails con HTML

### Frontend
- **Java Swing**: Interfaz gráfica de usuario
- **FlatLaf**: Look and Feel moderno
- **MigLayout**: Gestión avanzada de layouts
- **Raven DateTime**: Selector de fechas
- **Timing Framework**: Animaciones suaves

### Base de Datos
- **PostgreSQL 15**: Base de datos relacional
- **Docker**: Contenedorización de BD
- **pgAdmin 4**: Herramienta de administración

### Herramientas
- **Apache Ant**: Build tool
- **Docker Compose**: Orquestación de contenedores
- **Git**: Control de versiones

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- ✅ **Java JDK 11 o superior** - [Descargar](https://www.oracle.com/java/technologies/downloads/)
- ✅ **Docker Desktop** - [Descargar](https://www.docker.com/products/docker-desktop/)
- ✅ **Apache Ant** (incluido en el proyecto)
- ✅ **Git** (opcional) - [Descargar](https://git-scm.com/)

---

## 🚀 Inicio Rápido

### 1. Iniciar la Base de Datos

#### Windows:
```bash
start-database.bat
```

#### Linux/Mac:
```bash
docker-compose up -d
```

Esto iniciará:
- **PostgreSQL** en el puerto `5433`
- **pgAdmin** en `http://localhost:5050`

### 2. Acceder a pgAdmin (Opcional)

Si deseas administrar la base de datos visualmente:

1. Abre tu navegador en `http://localhost:5050`
2. Inicia sesión con:
   - **Email**: `admin@synapse.com`
   - **Password**: `admin123`
3. Conecta el servidor PostgreSQL:
   - Clic derecho en "Servers" → "Register" → "Server"
   - **General** → Name: `Synapse DB`
   - **Connection**:
     - Host: `postgres`
     - Port: `5432`
     - Database: `synapse_db`
     - Username: `postgres`
     - Password: `postgres`

### 3. Ejecutar la Aplicación

```bash
ant clean run
```

### 4. Iniciar Sesión

Credenciales por defecto:
- **Email**: `admin@synapse.com`
- **Password**: `admin123`

---

## 💡 Uso de la Aplicación

### Como Administrador

1. **Gestionar Usuarios**:
   - Ve a "Usuarios"
   - Crear nuevo usuario (se genera contraseña automática y se envía por email)
   - Editar información de usuarios existentes
   - Activar/desactivar usuarios con toggle
   - Asignar roles: Empleado, Gerente, Administrador

2. **Gestionar Equipos**:
   - Ve a "Equipos"
   - Crear equipos con nombre y descripción
   - Asignar miembros y líder
   - Editar o eliminar equipos

### Como Gerente

1. **Crear Tareas**:
   - Ve a "Tareas" → "Crear Nueva Tarea"
   - Completa los campos:
     - Título y descripción
     - Fecha límite
     - Prioridad (Baja, Media, Alta)
   - Selecciona tipo de asignación:
     - **Individual**: Asigna a un empleado específico
     - **Equipo**: Asigna a todos los miembros de un equipo
   - Adjunta archivos/imágenes (opcional)
   - Clic en "Crear Tarea"
   - ✉️ Se envía email automáticamente a los asignados

2. **Gestionar Equipos**:
   - Ve a "Mis Equipos"
   - Crea, edita o elimina equipos
   - Asigna miembros
   - Ver tareas del equipo

3. **Ver Tareas**:
   - Dashboard muestra todas las tareas creadas
   - Editar tareas existentes
   - Ver detalles completos
   - Archivar tareas completadas

### Como Empleado

1. **Ver Tareas Asignadas**:
   - Dashboard muestra tus tareas activas
   - Ver tareas por estado o prioridad

2. **Actualizar Tareas**:
   - Cambiar estado:
     - **Pendiente** → Clic en "Iniciar Tarea" → **En Progreso**
     - **En Progreso** → Clic en "Completar" → **Completada**
     - **En Progreso** → Clic en "Pausar" → **Pausada**
   - Ver detalles completos de cada tarea
   - El gerente recibe email cuando completas una tarea

3. **Gestionar Perfil**:
   - Actualizar información personal
   - Cambiar contraseña
   - Ver código de empleado

---

## 📁 Estructura del Proyecto

```
appTareas/
├── src/                          # Código fuente Java
│   └── com/synapse/
│       ├── core/                 # Núcleo de la aplicación
│       │   ├── models/           # Modelos de datos (Usuario, Tarea, Equipo)
│       │   └── services/         # Servicios de negocio
│       │       └── notifications/ # Sistema de notificaciones
│       ├── data/                 # Capa de acceso a datos
│       │   ├── dao/              # Data Access Objects
│       │   └── database/         # Conexión a BD (Singleton)
│       ├── ui/                   # Interfaz de usuario
│       │   ├── views/            # Vistas por rol (admin, gerente, empleado)
│       │   ├── components/       # Componentes reutilizables
│       │   └── lib/              # Librerías UI personalizadas
│       └── utils/                # Utilidades (PasswordBuilder, etc.)
├── resources/                    # Recursos de la aplicación
│   ├── database/                 # Scripts SQL
│   └── images/                   # Iconos e imágenes
├── lib/                          # Librerías externas (.jar)
├── docker/                       # Configuración Docker
│   ├── init-scripts/             # Scripts de inicialización de BD
│   │   └── 01-init-db.sql        # Schema y datos iniciales
│   └── README.md                 # Documentación Docker
├── Avance03_Patrones/            # Documentación académica
├── docker-compose.yml            # Configuración Docker Compose
├── build.xml                     # Configuración Apache Ant
├── start-database.bat            # Script para iniciar BD (Windows)
├── stop-database.bat             # Script para detener BD (Windows)
└── README.md                     # Este archivo
```

---

## 🔧 Configuración Avanzada

### Configuración de Email

Para habilitar el envío de emails, edita:
`src/com/synapse/core/services/notifications/EmailService.java`

```java
private static final String SMTP_HOST = "smtp.gmail.com";
private static final String SMTP_PORT = "587";
private static final String EMAIL_FROM = "tu-email@gmail.com";
private static final String EMAIL_PASSWORD = "tu-contraseña-app";
```

**Nota**: Para Gmail, necesitas crear una [contraseña de aplicación](https://support.google.com/accounts/answer/185833).

### Cambiar Puerto de Base de Datos

Si el puerto 5433 está en uso:

1. Edita `docker-compose.yml`:
```yaml
ports:
  - "5434:5432"  # Cambia 5433 por 5434
```

2. Edita `src/com/synapse/data/database/Conexion.java`:
```java
private static final String PUERTO = "5434";
```

### Reiniciar Base de Datos con Datos Limpios

```bash
docker-compose down -v  # Elimina volúmenes
docker-compose up -d    # Recrea con datos iniciales
```

---

## 🐛 Solución de Problemas

### La base de datos no inicia

**Síntomas**: Error al ejecutar `docker-compose up`

**Solución**:
```bash
# Ver logs para identificar el problema
docker-compose logs postgres

# Reiniciar Docker Desktop
# Luego:
docker-compose down
docker-compose up -d
```

### Puerto 5433 ya está en uso

**Solución**: Ver sección [Cambiar Puerto de Base de Datos](#cambiar-puerto-de-base-de-datos)

### Error de conexión a la base de datos

**Verificar**:
1. Docker está ejecutándose: `docker ps`
2. Base de datos está activa: `docker-compose ps`
3. Puerto correcto en `Conexion.java` (debe ser 5433)

**Solución**:
```bash
docker-compose restart
```

### La aplicación no compila

**Solución**:
```bash
# Limpiar y recompilar
ant clean
ant compile
ant run
```

### Emails no se envían

**Verificar**:
1. Configuración SMTP correcta en `EmailService.java`
2. Contraseña de aplicación válida (no la contraseña normal de Gmail)
3. Conexión a internet activa

---

## 📚 Documentación

Para más información detallada, consulta:

- 📖 **[Guía Docker](docker/README.md)** - Configuración detallada de PostgreSQL y pgAdmin
- 📊 **[Documentación de Patrones](Avance03_Patrones/Avance03_Patrones.md)** - Patrones de diseño implementados

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más información.

---

## 👨‍💻 Autor

Desarrollado con ❤️ para la gestión eficiente de tareas empresariales.

---

<div align="center">

### ⭐ Si te gusta este proyecto, dale una estrella!

**[⬆ Volver arriba](#-synapse---sistema-de-gestión-de-tareas)**

</div>
