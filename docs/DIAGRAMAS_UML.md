# 📊 Diagramas UML - Sistema Synapse

## Índice

1. [Diagrama de Casos de Uso](#1-diagrama-de-casos-de-uso)
2. [Diagrama de Clases](#2-diagrama-de-clases)
3. [Diagrama de Secuencia](#3-diagrama-de-secuencia)
4. [Diagrama de Base de Datos (ER)](#4-diagrama-de-base-de-datos-er)
5. [Diagrama de Componentes](#5-diagrama-de-componentes)

---

## 1. Diagrama de Casos de Uso

### Actores

- **Administrador**: Gestiona usuarios y equipos
- **Gerente**: Crea tareas y gestiona equipos
- **Empleado**: Gestiona sus tareas asignadas

### Casos de Uso

```mermaid
graph TB
    Admin[👨‍💼 Administrador]
    Gerente[👔 Gerente]
    Empleado[👨‍💻 Empleado]
    
    Admin --> CU1[Gestionar Usuarios]
    Admin --> CU2[Gestionar Equipos]
    Admin --> CU3[Ver Todas las Tareas]
    
    Gerente --> CU4[Crear Tarea]
    Gerente --> CU5[Editar Tarea]
    Gerente --> CU6[Asignar Tarea]
    Gerente --> CU7[Gestionar Mis Equipos]
    Gerente --> CU8[Ver Mis Tareas]
    
    Empleado --> CU9[Ver Tareas Asignadas]
    Empleado --> CU10[Actualizar Estado]
    Empleado --> CU11[Ver Detalles de Tarea]
    Empleado --> CU12[Cambiar Contraseña]
    
    CU6 --> CU13[Asignar a Empleado]
    CU6 --> CU14[Asignar a Equipo]
    
    CU4 --> CU15[Adjuntar Archivos]
    CU4 --> CU16[Enviar Notificación]
```

---

## 2. Diagrama de Clases

### Modelos de Dominio

```mermaid
classDiagram
    class Usuario {
        -int idUsuario
        -String nombre
        -String email
        -String fotoUrl
        -boolean activo
        -String codigoEmpleado
        -int idRol
        -String nombreRol
        +Usuario(Builder)
        +getters()
        +setters()
    }
    
    class Tarea {
        -int idTarea
        -String titulo
        -String descripcion
        -Timestamp fechaCreacion
        -Timestamp fechaLimite
        -int idCreador
        -int idPrioridad
        -int idEstado
        -Usuario creador
        -List~Usuario~ usuariosAsignados
        -List~Equipo~ equiposAsignados
        -List~Adjunto~ adjuntos
        +Tarea(Builder)
        +getters()
        +setters()
    }
    
    class Equipo {
        -int idEquipo
        -String nombre
        -String descripcion
        -int idLider
        -boolean activo
        -Usuario lider
        -List~Usuario~ miembros
        +Equipo()
        +getters()
        +setters()
    }
    
    class Adjunto {
        -int idAdjunto
        -int idTarea
        -String nombreArchivo
        -String rutaArchivo
        -String tipoArchivo
        -Timestamp fechaSubida
        +Adjunto()
        +getters()
        +setters()
    }
    
    Usuario "1" -- "*" Tarea : crea
    Usuario "*" -- "*" Tarea : asignado a
    Equipo "*" -- "*" Tarea : asignado a
    Equipo "1" -- "*" Usuario : tiene miembros
    Tarea "1" -- "*" Adjunto : tiene
```

### Capa DAO

```mermaid
classDiagram
    class TareaDAO {
        +boolean crear(Tarea)
        +Tarea obtenerPorId(int)
        +List~Tarea~ obtenerTodas()
        +boolean actualizar(Tarea)
        +boolean eliminar(int)
        +List~Usuario~ getUsuariosAsignadosPorTarea(int)
        +List~Equipo~ getEquiposAsignadosPorTarea(int)
        +boolean asignarUsuario(int, int)
        +boolean asignarEquipo(int, int)
    }
    
    class UsuarioDAO {
        +boolean crear(Usuario, String, int)
        +Usuario obtenerPorId(int)
        +List~Usuario~ getUsuarios()
        +boolean actualizar(Usuario)
        +Usuario login(String, String)
        +List~Usuario~ getUsuariosPorRol(int)
    }
    
    class EquipoDAO {
        +int crear(Equipo)
        +Equipo obtenerPorId(int)
        +List~Equipo~ getEquipos()
        +boolean actualizar(Equipo)
        +boolean eliminar(int)
        +List~Usuario~ getMiembros(int)
        +boolean agregarMiembro(int, int)
        +boolean quitarMiembro(int, int)
        +List~Equipo~ getEquiposPorLider(int)
    }
    
    class Conexion {
        -static Conexion instancia
        -String USUARIO
        -String CONTRASENA
        -String BD
        -String PUERTO
        +static getInstance()
        +Connection getConnection()
    }
    
    TareaDAO ..> Conexion : usa
    UsuarioDAO ..> Conexion : usa
    EquipoDAO ..> Conexion : usa
```

### Servicios

```mermaid
classDiagram
    class TareaService {
        -TareaDAO tareaDAO
        +boolean crearTareaCompleta(Tarea, Integer, Integer)
        +boolean actualizarTareaCompleta(Tarea, Integer, Integer)
        +List~Tarea~ getTareasConDetalles(int)
    }
    
    class EmailService {
        +boolean enviarEmailAsignacion(Tarea, Usuario, List~File~)
        +boolean sendEmail(String, String, String, List~File~)
        +void verificarVencimientos()
    }
    
    class EmailTemplates {
        +static String getTemplateAsignacionTarea(...)
    }
    
    class PasswordHasher {
        +static String hashPassword(String)
        +static boolean checkPassword(String, String)
    }
    
    TareaService ..> TareaDAO : usa
    EmailService ..> EmailTemplates : usa
```

---

## 3. Diagrama de Secuencia

### Crear Tarea y Enviar Notificación

```mermaid
sequenceDiagram
    actor Gerente
    participant UI as formCrearTarea
    participant TS as TareaService
    participant DAO as TareaDAO
    participant ES as EmailService
    participant BD as Base de Datos
    
    Gerente->>UI: Completa formulario
    Gerente->>UI: Adjunta archivos
    Gerente->>UI: Click "Crear Tarea"
    
    UI->>UI: Validar datos
    UI->>TS: crearTareaCompleta(tarea, idUsuario, idEquipo)
    
    TS->>DAO: crear(tarea)
    DAO->>BD: INSERT INTO tareas
    BD-->>DAO: idTarea
    DAO-->>TS: true
    
    alt Asignación Individual
        TS->>DAO: asignarUsuario(idTarea, idUsuario)
        DAO->>BD: INSERT INTO tarea_usuario
        BD-->>DAO: OK
    else Asignación a Equipo
        TS->>DAO: asignarEquipo(idTarea, idEquipo)
        DAO->>BD: INSERT INTO tarea_equipo
        BD-->>DAO: OK
    end
    
    TS-->>UI: true
    
    UI->>ES: enviarEmailAsignacion(tarea, usuario, adjuntos)
    
    alt Asignación Individual
        ES->>ES: Crear email HTML
        ES->>ES: Adjuntar archivos
        ES->>ES: Enviar email
    else Asignación a Equipo
        ES->>DAO: getMiembros(idEquipo)
        DAO-->>ES: List<Usuario>
        loop Para cada miembro
            ES->>ES: Crear email HTML
            ES->>ES: Adjuntar archivos
            ES->>ES: Enviar email
        end
    end
    
    ES-->>UI: true
    UI->>Gerente: Mostrar confirmación
```

### Login

```mermaid
sequenceDiagram
    actor Usuario
    participant UI as Login
    participant DAO as UsuarioDAO
    participant PH as PasswordHasher
    participant BD as Base de Datos
    
    Usuario->>UI: Ingresa email y password
    Usuario->>UI: Click "Iniciar Sesión"
    
    UI->>DAO: login(email, password)
    DAO->>BD: SELECT * FROM usuarios WHERE email = ?
    BD-->>DAO: Usuario + hash
    
    DAO->>PH: checkPassword(password, hash)
    PH-->>DAO: boolean
    
    alt Password correcto
        DAO-->>UI: Usuario
        UI->>UI: Guardar sesión
        UI->>Usuario: Redirigir a Dashboard
    else Password incorrecto
        DAO-->>UI: null
        UI->>Usuario: Mostrar error
    end
```

---

## 4. Diagrama de Base de Datos (ER)

```mermaid
erDiagram
    ROLES ||--o{ CREDENCIALES : tiene
    USUARIOS ||--o{ CREDENCIALES : tiene
    USUARIOS ||--o{ TAREAS : crea
    USUARIOS ||--o{ EQUIPOS : lidera
    USUARIOS }o--o{ EQUIPO_MIEMBROS : pertenece
    EQUIPOS }o--o{ EQUIPO_MIEMBROS : tiene
    TAREAS }o--o{ TAREA_USUARIO : asignada_a
    USUARIOS }o--o{ TAREA_USUARIO : asignado
    TAREAS }o--o{ TAREA_EQUIPO : asignada_a
    EQUIPOS }o--o{ TAREA_EQUIPO : asignado
    TAREAS ||--o{ ADJUNTOS : tiene
    PRIORIDADES ||--o{ TAREAS : tiene
    ESTADOS_TAREA ||--o{ TAREAS : tiene
    
    ROLES {
        int id_rol PK
        string nombre_rol UK
    }
    
    USUARIOS {
        int id_usuario PK
        string nombre
        string email UK
        string foto_url
        boolean activo
        string codigo_empleado UK
    }
    
    CREDENCIALES {
        int id_credencial PK
        int id_usuario FK
        string password
        int id_rol FK
    }
    
    EQUIPOS {
        int id_equipo PK
        string nombre
        string descripcion
        int id_lider FK
        boolean activo
    }
    
    EQUIPO_MIEMBROS {
        int id_equipo PK,FK
        int id_usuario PK,FK
    }
    
    TAREAS {
        int id_tarea PK
        string titulo
        string descripcion
        timestamp fecha_creacion
        timestamp fecha_limite
        int id_creador FK
        int id_prioridad FK
        int id_estado FK
    }
    
    TAREA_USUARIO {
        int id_tarea PK,FK
        int id_usuario PK,FK
    }
    
    TAREA_EQUIPO {
        int id_tarea PK,FK
        int id_equipo PK,FK
    }
    
    ADJUNTOS {
        int id_adjunto PK
        int id_tarea FK
        string nombre_archivo
        string ruta_archivo
        string tipo_archivo
        timestamp fecha_subida
    }
    
    PRIORIDADES {
        int id_prioridad PK
        string nombre_prioridad UK
    }
    
    ESTADOS_TAREA {
        int id_estado PK
        string nombre_estado UK
    }
```

---

## 5. Diagrama de Componentes

```mermaid
graph TB
    subgraph "Capa de Presentación"
        UI_ADMIN[Vistas Admin]
        UI_GERENTE[Vistas Gerente]
        UI_EMPLEADO[Vistas Empleado]
        UI_SHARED[Vistas Compartidas]
        COMPONENTS[Componentes Reutilizables]
    end
    
    subgraph "Capa de Servicios"
        TAREA_SERVICE[TareaService]
        EMAIL_SERVICE[EmailService]
        EMAIL_TEMPLATES[EmailTemplates]
    end
    
    subgraph "Capa de Datos"
        TAREA_DAO[TareaDAO]
        USUARIO_DAO[UsuarioDAO]
        EQUIPO_DAO[EquipoDAO]
        CONEXION[Conexion]
    end
    
    subgraph "Capa de Persistencia"
        POSTGRES[(PostgreSQL)]
        DOCKER[Docker Container]
    end
    
    subgraph "Utilidades"
        PASSWORD_HASHER[PasswordHasher]
        EMAIL_CONFIG[EmailConfig]
    end
    
    UI_ADMIN --> TAREA_SERVICE
    UI_GERENTE --> TAREA_SERVICE
    UI_EMPLEADO --> TAREA_SERVICE
    
    UI_ADMIN --> USUARIO_DAO
    UI_GERENTE --> EQUIPO_DAO
    
    TAREA_SERVICE --> TAREA_DAO
    TAREA_SERVICE --> EMAIL_SERVICE
    
    EMAIL_SERVICE --> EMAIL_TEMPLATES
    EMAIL_SERVICE --> EMAIL_CONFIG
    
    TAREA_DAO --> CONEXION
    USUARIO_DAO --> CONEXION
    EQUIPO_DAO --> CONEXION
    
    USUARIO_DAO --> PASSWORD_HASHER
    
    CONEXION --> POSTGRES
    DOCKER --> POSTGRES
    
    style UI_ADMIN fill:#e1f5ff
    style UI_GERENTE fill:#e1f5ff
    style UI_EMPLEADO fill:#e1f5ff
    style TAREA_SERVICE fill:#fff4e1
    style EMAIL_SERVICE fill:#fff4e1
    style TAREA_DAO fill:#e8f5e9
    style USUARIO_DAO fill:#e8f5e9
    style EQUIPO_DAO fill:#e8f5e9
    style POSTGRES fill:#f3e5f5
```

---

## Flujos de Datos Principales

### 1. Flujo de Creación de Tarea

```
Gerente → formCrearTarea → TareaService → TareaDAO → PostgreSQL
                ↓
         EmailService → SMTP Server → Usuario(s)
```

### 2. Flujo de Autenticación

```
Usuario → Login → UsuarioDAO → PostgreSQL
                      ↓
              PasswordHasher (BCrypt)
                      ↓
              Dashboard (según rol)
```

### 3. Flujo de Gestión de Equipos

```
Gerente → formMisEquipos → EquipoDAO → PostgreSQL
                ↓
         cardEquipo (componente)
                ↓
         Ver/Editar/Eliminar
```

---

## Patrones de Diseño Utilizados

### 1. Singleton
- **Dónde**: `Conexion.java`
- **Por qué**: Una sola instancia de configuración de BD

### 2. DAO (Data Access Object)
- **Dónde**: `TareaDAO`, `UsuarioDAO`, `EquipoDAO`
- **Por qué**: Separar lógica de acceso a datos

### 3. Builder
- **Dónde**: `Tarea.Builder`
- **Por qué**: Construcción flexible de objetos complejos

### 4. MVC (Model-View-Controller)
- **Dónde**: Toda la aplicación
- **Por qué**: Separación de responsabilidades

### 5. Template Method
- **Dónde**: `EmailTemplates`
- **Por qué**: Plantillas reutilizables de emails

---

## Convenciones de Código

### Nomenclatura

- **Clases**: PascalCase (`TareaService`)
- **Métodos**: camelCase (`crearTarea`)
- **Constantes**: UPPER_SNAKE_CASE (`SMTP_HOST`)
- **Variables**: camelCase (`idUsuario`)

### Estructura de Paquetes

```
com.synapse
├── core          # Núcleo del sistema
├── data          # Acceso a datos
├── ui            # Interfaz de usuario
└── utils         # Utilidades
```

---

**Versión**: 2.0  
**Última actualización**: Noviembre 2025  
**Herramientas**: Mermaid, PlantUML
