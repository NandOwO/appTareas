# 🏗️ Documentación Técnica - Sistema Synapse

## Índice

1. [Arquitectura del Sistema](#1-arquitectura-del-sistema)
2. [Tecnologías Utilizadas](#2-tecnologías-utilizadas)
3. [Estructura del Proyecto](#3-estructura-del-proyecto)
4. [Base de Datos](#4-base-de-datos)
5. [Capa de Datos (DAO)](#5-capa-de-datos-dao)
6. [Servicios](#6-servicios)
7. [Interfaz de Usuario](#7-interfaz-de-usuario)
8. [Seguridad](#8-seguridad)
9. [Configuración](#9-configuración)

---

## 1. Arquitectura del Sistema

### Patrón de Arquitectura

El sistema utiliza una **arquitectura en capas** (Layered Architecture):

```
┌─────────────────────────────────────┐
│     Capa de Presentación (UI)      │
│         Java Swing + FlatLaf        │
├─────────────────────────────────────┤
│      Capa de Lógica de Negocio     │
│           Services Layer            │
├─────────────────────────────────────┤
│       Capa de Acceso a Datos       │
│              DAO Layer              │
├─────────────────────────────────────┤
│         Capa de Persistencia       │
│      PostgreSQL + JDBC Driver       │
└─────────────────────────────────────┘
```

### Principios de Diseño

- **Separación de Responsabilidades**: Cada capa tiene una responsabilidad específica
- **Singleton Pattern**: Para conexiones de BD y servicios
- **DAO Pattern**: Para acceso a datos
- **Builder Pattern**: Para construcción de objetos complejos (Tarea)
- **MVC Pattern**: En la capa de presentación

---

## 2. Tecnologías Utilizadas

### Backend

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 11+ | Lenguaje principal |
| **JDBC** | 4.2 | Conexión a base de datos |
| **PostgreSQL Driver** | 42.6.0 | Driver de PostgreSQL |
| **BCrypt** | 0.10.2 | Encriptación de contraseñas |
| **JavaMail** | 1.6.2 | Envío de emails |

### Frontend

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java Swing** | Built-in | Framework de UI |
| **FlatLaf** | 3.2.5 | Look and Feel moderno |
| **MigLayout** | 5.3 | Gestor de layouts |
| **Raven DateTime** | 1.0 | Selector de fechas |

### Base de Datos

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **PostgreSQL** | 15 | Base de datos relacional |
| **Docker** | Latest | Contenedorización de BD |

### Build Tools

| Herramienta | Propósito |
|-------------|-----------|
| **Apache Ant** | Build automation |
| **Docker Compose** | Orquestación de contenedores |

---

## 3. Estructura del Proyecto

```
appTareas/
├── src/
│   └── com/synapse/
│       ├── core/                    # Núcleo del sistema
│       │   ├── models/              # Modelos de dominio
│       │   │   ├── Usuario.java
│       │   │   ├── Tarea.java
│       │   │   ├── Equipo.java
│       │   │   ├── Adjunto.java
│       │   │   └── Rol.java
│       │   └── services/            # Servicios de negocio
│       │       ├── TareaService.java
│       │       ├── UsuarioService.java
│       │       └── notifications/
│       │           ├── EmailService.java
│       │           ├── EmailConfig.java
│       │           └── EmailTemplates.java
│       ├── data/                    # Capa de datos
│       │   ├── dao/                 # Data Access Objects
│       │   │   ├── TareaDAO.java
│       │   │   ├── UsuarioDAO.java
│       │   │   └── EquipoDAO.java
│       │   └── database/            # Configuración de BD
│       │       └── Conexion.java
│       ├── ui/                      # Interfaz de usuario
│       │   ├── views/               # Vistas por rol
│       │   │   ├── admin/           # Vistas de administrador
│       │   │   ├── gerente/         # Vistas de gerente
│       │   │   ├── empleado/        # Vistas de empleado
│       │   │   └── shared/          # Vistas compartidas
│       │   └── components/          # Componentes reutilizables
│       │       ├── cardEquipo.java
│       │       ├── TaskActionsEditor.java
│       │       └── PrioridadPillRenderer.java
│       └── utils/                   # Utilidades
│           ├── PasswordHasher.java
│           └── GeneratePasswordHash.java
├── resources/                       # Recursos
│   ├── database/                    # Scripts SQL
│   │   ├── schema_complete.sql
│   │   └── test_data.sql
│   └── images/                      # Imágenes de la app
├── lib/                             # Librerías externas
├── docker/                          # Configuración Docker
│   ├── init-scripts/
│   │   └── 01-init-db.sql
│   └── README.md
├── docs/                            # Documentación
├── docker-compose.yml
├── build.xml
└── README.md
```

---

## 4. Base de Datos

### Esquema de Base de Datos

#### Tablas Principales

**1. roles**
```sql
CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);
```

**2. usuarios**
```sql
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    foto_url VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    codigo_empleado VARCHAR(50) UNIQUE
);
```

**3. credenciales**
```sql
CREATE TABLE credenciales (
    id_credencial SERIAL PRIMARY KEY,
    id_usuario INT REFERENCES usuarios(id_usuario),
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);
```

**4. equipos**
```sql
CREATE TABLE equipos (
    id_equipo SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_lider INT,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_lider) REFERENCES usuarios(id_usuario)
);
```

**5. equipo_miembros**
```sql
CREATE TABLE equipo_miembros (
    id_equipo INT,
    id_usuario INT,
    PRIMARY KEY (id_equipo, id_usuario),
    FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);
```

**6. tareas**
```sql
CREATE TABLE tareas (
    id_tarea SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_limite TIMESTAMP,
    id_creador INT NOT NULL,
    id_prioridad INT DEFAULT 2,
    id_estado INT DEFAULT 1,
    FOREIGN KEY (id_creador) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_prioridad) REFERENCES prioridades(id_prioridad),
    FOREIGN KEY (id_estado) REFERENCES estados_tarea(id_estado)
);
```

**7. tarea_usuario**
```sql
CREATE TABLE tarea_usuario (
    id_tarea INT,
    id_usuario INT,
    PRIMARY KEY (id_tarea, id_usuario),
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);
```

**8. tarea_equipo**
```sql
CREATE TABLE tarea_equipo (
    id_tarea INT,
    id_equipo INT,
    PRIMARY KEY (id_tarea, id_equipo),
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE,
    FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo) ON DELETE CASCADE
);
```

### Diagrama de Relaciones

Ver: [DIAGRAMAS_UML.md](DIAGRAMAS_UML.md)

---

## 5. Capa de Datos (DAO)

### Patrón DAO

Cada entidad tiene su propio DAO que encapsula toda la lógica de acceso a datos.

#### TareaDAO

**Métodos principales**:
```java
public class TareaDAO {
    // CRUD básico
    public boolean crear(Tarea tarea);
    public Tarea obtenerPorId(int idTarea);
    public List<Tarea> obtenerTodas();
    public boolean actualizar(Tarea tarea);
    public boolean eliminar(int idTarea);
    
    // Asignaciones
    public List<Usuario> getUsuariosAsignadosPorTarea(int idTarea);
    public List<Equipo> getEquiposAsignadosPorTarea(int idTarea);
    public boolean asignarUsuario(int idTarea, int idUsuario);
    public boolean asignarEquipo(int idTarea, int idEquipo);
    
    // Consultas específicas
    public List<Tarea> getTareasPorUsuario(int idUsuario);
    public List<Tarea> getTareasPorEquipo(int idEquipo);
    public List<Tarea> getTareasPorCreador(int idCreador);
}
```

#### UsuarioDAO

**Métodos principales**:
```java
public class UsuarioDAO {
    // CRUD
    public boolean crear(Usuario usuario, String password, int idRol);
    public Usuario obtenerPorId(int idUsuario);
    public List<Usuario> getUsuarios();
    public boolean actualizar(Usuario usuario);
    
    // Autenticación
    public Usuario login(String email, String password);
    
    // Roles
    public List<Usuario> getUsuariosPorRol(int idRol);
    
    // Búsqueda
    public Usuario buscarPorEmail(String email);
}
```

#### EquipoDAO

**Métodos principales**:
```java
public class EquipoDAO {
    // CRUD
    public int crear(Equipo equipo);
    public Equipo obtenerPorId(int idEquipo);
    public List<Equipo> getEquipos();
    public boolean actualizar(Equipo equipo);
    public boolean eliminar(int idEquipo);
    
    // Miembros
    public List<Usuario> getMiembros(int idEquipo);
    public boolean agregarMiembro(int idEquipo, int idUsuario);
    public boolean quitarMiembro(int idEquipo, int idUsuario);
    public int contarMiembros(int idEquipo);
    
    // Consultas específicas
    public List<Equipo> getEquiposPorLider(int idLider);
}
```

### Gestión de Conexiones

```java
public class Conexion {
    private static Conexion instancia;
    
    // Configuración
    private final String USUARIO = "postgres";
    private final String CONTRASENA = "postgres";
    private final String BD = "synapse_db";
    private final String PUERTO = "5433";
    
    // Singleton
    public static Conexion getInstance();
    
    // Obtener nueva conexión
    public Connection getConnection() throws SQLException;
}
```

**Importante**: Cada llamada a `getConnection()` crea una **nueva conexión**. Usar con `try-with-resources` para cerrar automáticamente.

---

## 6. Servicios

### TareaService

Encapsula la lógica de negocio para tareas.

```java
public class TareaService {
    private TareaDAO tareaDAO;
    
    // Crear tarea completa con asignación
    public boolean crearTareaCompleta(Tarea tarea, Integer idUsuario, Integer idEquipo);
    
    // Actualizar tarea completa
    public boolean actualizarTareaCompleta(Tarea tarea, Integer idUsuario, Integer idEquipo);
    
    // Obtener tareas con información completa
    public List<Tarea> getTareasConDetalles(int idUsuario);
}
```

### EmailService

Gestiona el envío de notificaciones por email.

```java
public class EmailService {
    // Enviar email de asignación
    public boolean enviarEmailAsignacion(Tarea tarea, Usuario usuario, List<File> adjuntos);
    
    // Enviar email genérico con HTML
    public boolean sendEmail(String to, String subject, String htmlBody, List<File> attachments);
    
    // Verificar vencimientos próximos
    public void verificarVencimientos();
}
```

**Configuración**: Ver `EmailConfig.java`

### EmailTemplates

Plantillas HTML para emails.

```java
public class EmailTemplates {
    // Template de asignación de tarea
    public static String getTemplateAsignacionTarea(
        String nombreUsuario,
        String tituloTarea,
        String descripcion,
        String fechaLimite,
        boolean tieneAdjuntos,
        String listaAdjuntos
    );
}
```

---

## 7. Interfaz de Usuario

### Estructura de Vistas

#### Por Rol

**Admin**:
- `DashboardAdmin.java` - Panel principal
- `formGestionUsuarios.java` - Gestión de usuarios
- `formGestionEquipos.java` - Gestión de equipos
- `dialogCrearEquipo.java` - Crear equipo
- `dialogEditarEquipo.java` - Editar equipo

**Gerente**:
- `DashboardGerente.java` - Panel principal
- `formCrearTarea.java` - Crear tarea
- `formMisEquipos.java` - Gestionar equipos
- `EditarTareaDialog.java` - Editar tarea

**Empleado**:
- `DashboardEmpleado.java` - Panel principal
- `formTareas.java` - Ver tareas
- `VerTareaDialog.java` - Ver detalles

### Componentes Reutilizables

**cardEquipo.java**:
```java
public class cardEquipo extends JPanel {
    private Equipo equipo;
    
    // Botones
    - Ver Miembros
    - Editar Equipo
    - Eliminar Equipo
}
```

**TaskActionsEditor.java**:
```java
public class TaskActionsEditor extends AbstractCellEditor {
    // Renderiza botones de acción en tabla
    - Ver
    - Editar
    - Eliminar
}
```

**PrioridadPillRenderer.java**:
```java
public class PrioridadPillRenderer extends JLabel {
    // Renderiza prioridad con color
    - Baja: Verde
    - Media: Amarillo
    - Alta: Rojo
}
```

### Look and Feel

**FlatLaf**: Tema moderno y profesional

```java
// Configuración global
FlatLightLaf.setup();

// Estilos personalizados
component.putClientProperty(FlatClientProperties.STYLE,
    "arc:10;borderWidth:1;focusWidth:1");
```

---

## 8. Seguridad

### Encriptación de Contraseñas

**BCrypt** con factor de trabajo 10:

```java
public class PasswordHasher {
    private static final int WORK_FACTOR = 10;
    
    // Hashear contraseña
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }
    
    // Verificar contraseña
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
```

### Autenticación

```java
// Login
Usuario usuario = usuarioDAO.login(email, password);
if (usuario != null) {
    // Autenticado
    // Guardar en sesión
}
```

### Control de Acceso

- **Roles**: Admin, Gerente, Empleado
- **Permisos**: Verificados en cada vista
- **Sesión**: Usuario logueado guardado en memoria

---

## 9. Configuración

### Base de Datos (Docker)

**docker-compose.yml**:
```yaml
services:
  postgres:
    image: postgres:15-alpine
    ports:
      - "5433:5432"
    environment:
      POSTGRES_DB: synapse_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
```

### Conexión a BD

**Conexion.java**:
```java
private final String USUARIO = "postgres";
private final String CONTRASENA = "postgres";
private final String BD = "synapse_db";
private final String PUERTO = "5433";
```

### Email

**EmailConfig.java**:
```java
private static final String SMTP_HOST = "smtp.gmail.com";
private static final String SMTP_PORT = "587";
private static final String USERNAME = "tu-email@gmail.com";
private static final String PASSWORD = "tu-app-password";
```

### Build

**build.xml**:
```xml
<project name="Synapse" default="compile">
    <property name="src.dir" value="src"/>
    <property name="build.dir" value="build"/>
    <property name="lib.dir" value="lib"/>
    
    <target name="compile">
        <javac srcdir="${src.dir}" destdir="${build.dir}">
            <classpath>
                <fileset dir="${lib.dir}" includes="**/*.jar"/>
            </classpath>
        </javac>
    </target>
</project>
```

---

## Dependencias Externas

### Librerías Requeridas

```
lib/
├── flatlaf-3.2.5.jar              # Look and Feel
├── miglayout-swing-5.3.jar        # Layout Manager
├── postgresql-42.6.0.jar          # PostgreSQL Driver
├── jbcrypt-0.4.jar                # BCrypt
├── javax.mail.jar                 # JavaMail
├── activation.jar                 # JavaMail Activation
└── raven-datetime-1.0.jar         # Date Picker
```

---

## Flujo de Datos

### Crear Tarea

```
Usuario (Gerente)
    ↓
formCrearTarea.java
    ↓
TareaService.crearTareaCompleta()
    ↓
TareaDAO.crear() + asignarUsuario/Equipo()
    ↓
Base de Datos (INSERT)
    ↓
EmailService.enviarEmailAsignacion()
    ↓
Usuario(s) recibe(n) email
```

### Login

```
Usuario ingresa credenciales
    ↓
UsuarioDAO.login(email, password)
    ↓
BCrypt.checkpw(password, hash)
    ↓
Si válido: Retorna Usuario
    ↓
Redirige a Dashboard según rol
```

---

## Mejores Prácticas Implementadas

1. **Try-with-resources**: Para cerrar conexiones automáticamente
2. **Prepared Statements**: Prevenir SQL Injection
3. **Singleton Pattern**: Para servicios y conexiones
4. **Builder Pattern**: Para objetos complejos
5. **SwingWorker**: Para operaciones asíncronas en UI
6. **Separación de capas**: UI, Servicios, DAO, BD

---

**Versión**: 2.0  
**Última actualización**: Noviembre 2025  
**Autor**: Sistema Synapse
