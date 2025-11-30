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
- [Instalación y Configuración](#-instalación-y-configuración)
- [Uso de la Aplicación](#-uso-de-la-aplicación)
- [Documentación](#-documentación)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Solución de Problemas](#-solución-de-problemas)

---

## 🎯 Descripción

**Synapse** es un sistema de gestión de tareas empresarial diseñado para facilitar la organización, asignación y seguimiento de tareas en equipos de trabajo. La aplicación permite a los gerentes crear y asignar tareas a empleados individuales o equipos completos, con notificaciones automáticas por email y adjuntos de archivos.

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
- Asignar roles
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
- ✅ **Visualización detallada**: Ver toda la información de una tarea

### 👥 Gestión de Equipos

- ✅ **Crear equipos** con nombre y descripción
- ✅ **Asignar miembros** de forma visual
- ✅ **Líder de equipo**: Cada equipo tiene un gerente asignado
- ✅ **Editar equipos**: Modificar nombre y descripción
- ✅ **Ver miembros**: Lista completa de integrantes
- ✅ **Eliminar equipos**: Con confirmación de seguridad

### 📧 Notificaciones Automáticas

- ✅ **Email al asignar tarea**: Notificación automática
- ✅ **Asignación individual**: Email al empleado
- ✅ **Asignación a equipo**: Email a todos los miembros
- ✅ **Adjuntos incluidos**: Archivos e imágenes en el email
- ✅ **Información completa**: Título, descripción, fecha límite

### 🎨 Interfaz de Usuario

- ✅ **Diseño moderno**: Interfaz limpia y profesional
- ✅ **FlatLaf Look and Feel**: Apariencia moderna
- ✅ **Responsive**: Adaptable a diferentes tamaños
- ✅ **Iconos intuitivos**: Fácil navegación
- ✅ **Notificaciones toast**: Feedback visual inmediato

---

## 🛠️ Tecnologías

### Backend
- **Java 11+**: Lenguaje principal
- **JDBC**: Conexión a base de datos
- **BCrypt**: Encriptación de contraseñas
- **JavaMail**: Envío de emails

### Frontend
- **Java Swing**: Interfaz gráfica
- **FlatLaf**: Look and Feel moderno
- **MigLayout**: Gestión de layouts
- **Raven DateTime**: Selector de fechas

### Base de Datos
- **PostgreSQL 15**: Base de datos relacional
- **Docker**: Contenedorización de BD

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

## 🚀 Instalación y Configuración

### Paso 1: Clonar o Descargar el Proyecto

```bash
git clone https://github.com/tu-usuario/synapse-tareas.git
cd synapse-tareas
```

O descarga el ZIP y extráelo.

### Paso 2: Iniciar la Base de Datos

#### Opción A: Usando el script (Recomendado)

```bash
start-database.bat
```

#### Opción B: Usando Docker Compose

```bash
docker-compose up -d
```

**Nota**: La base de datos se ejecuta en el puerto **5433** para evitar conflictos con PostgreSQL local.

### Paso 3: Verificar que la BD esté ejecutándose

```bash
docker-compose ps
```

Deberías ver:
```
NAME          IMAGE                  STATUS
synapse_db    postgres:15-alpine     Up
```

### Paso 4: Compilar y Ejecutar la Aplicación

```bash
# Compilar
ant clean compile

# Ejecutar
ant run
```

O en un solo comando:
```bash
ant clean run
```

### Paso 5: Iniciar Sesión

Usa las credenciales del administrador:

```
📧 Email: admin@synapse.com
🔑 Contraseña: admin123
```

---

## 💡 Uso de la Aplicación

### Primer Inicio - Administrador

1. **Inicia sesión** con `admin@synapse.com` / `admin123`
2. **Crea usuarios**:
   - Ve a "Gestión de Usuarios"
   - Clic en "Crear Usuario"
   - Completa el formulario (nombre, email, rol)
   - La contraseña predeterminada es el email del usuario
3. **Crea equipos** (si tienes gerentes):
   - Ve a "Gestión de Equipos"
   - Clic en "Crear Equipo"
   - Asigna un líder y miembros

### Como Gerente

1. **Crear Tareas**:
   - Ve a "Crear Tarea"
   - Completa: título, descripción, fecha límite, prioridad
   - Selecciona tipo de asignación:
     - **Individual**: Asigna a un empleado específico
     - **Equipo**: Asigna a todos los miembros de un equipo
   - Adjunta archivos/imágenes (opcional)
   - Clic en "Crear Tarea"
   - ✉️ Se envía email automáticamente

2. **Gestionar Equipos**:
   - Ve a "Mis Equipos"
   - Crea, edita o elimina equipos
   - Ver miembros de cada equipo

3. **Ver Tareas**:
   - Dashboard muestra todas las tareas creadas
   - Editar tareas existentes
   - Ver detalles completos

### Como Empleado

1. **Ver Tareas Asignadas**:
   - Dashboard muestra tus tareas
   - Filtrar por estado o prioridad

2. **Actualizar Tareas**:
   - Cambiar estado (Pendiente → En Progreso → Completada)
   - Ver detalles completos
   - Ver archivos adjuntos (enviados por email)

3. **Gestionar Perfil**:
   - Actualizar información personal
   - Cambiar contraseña

---

## 📚 Documentación

Para más información detallada, consulta:

- 📖 **[Manual de Usuario](docs/MANUAL_USUARIO.md)** - Guía completa de uso
- 🏗️ **[Documentación Técnica](docs/DOCUMENTACION_TECNICA.md)** - Arquitectura y diseño
- 📊 **[Diagramas UML](docs/DIAGRAMAS_UML.md)** - Diagramas del sistema
- 🐳 **[Guía Docker](docker/README.md)** - Configuración de base de datos

---

## 📁 Estructura del Proyecto

```
synapse-tareas/
├── src/                          # Código fuente Java
│   └── com/synapse/
│       ├── core/                 # Modelos y servicios
│       │   ├── models/           # Entidades (Usuario, Tarea, Equipo)
│       │   └── services/         # Lógica de negocio
│       ├── data/                 # Capa de datos
│       │   ├── dao/              # Data Access Objects
│       │   └── database/         # Conexión a BD
│       ├── ui/                   # Interfaz de usuario
│       │   ├── views/            # Vistas por rol
│       │   └── components/       # Componentes reutilizables
│       └── utils/                # Utilidades
├── resources/                    # Recursos
│   ├── database/                 # Scripts SQL
│   └── images/                   # Imágenes de la app
├── lib/                          # Librerías externas
├── docker/                       # Configuración Docker
│   ├── init-scripts/             # Scripts de inicialización
│   └── README.md                 # Documentación Docker
├── docs/                         # Documentación
├── docker-compose.yml            # Configuración Docker Compose
├── build.xml                     # Configuración Apache Ant
├── start-database.bat            # Script inicio BD
└── stop-database.bat             # Script detener BD
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

Si necesitas cambiar el puerto, edita:

1. `docker-compose.yml` - Cambia `"5433:5432"`
2. `src/com/synapse/data/database/Conexion.java` - Cambia `PUERTO = "5433"`

---

## 🐛 Solución de Problemas

### La base de datos no inicia

**Problema**: Error al ejecutar `docker-compose up`

**Solución**:
```bash
# Ver logs
docker-compose logs postgres

# Reiniciar Docker Desktop
# Luego:
docker-compose down
docker-compose up -d
```

### Puerto 5433 ya está en uso

**Solución**: Cambia el puerto en `docker-compose.yml`:
```yaml
ports:
  - "5434:5432"  # Usa 5434 en lugar de 5433
```

Y actualiza `Conexion.java` con el nuevo puerto.

### Error de conexión a la base de datos

**Verificar**:
1. Docker está ejecutándose: `docker ps`
2. Base de datos está activa: `docker-compose ps`
3. Puerto correcto en `Conexion.java`

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
```

### Olvidé la contraseña del admin

**Solución**: Reinicia la base de datos:
```bash
docker-compose down -v
docker-compose up -d
```

Esto recreará el usuario admin con contraseña `admin123`.

---

## 📝 Credenciales de Acceso

### Base de Datos
```
Host: localhost
Puerto: 5433
Base de Datos: synapse_db
Usuario: postgres
Contraseña: postgres
```

### Usuario Administrador
```
Email: admin@synapse.com
Contraseña: admin123
Rol: Administrador
```

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

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
