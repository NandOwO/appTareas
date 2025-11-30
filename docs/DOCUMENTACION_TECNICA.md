# Documentación Técnica - Sistema de Gestión de Tareas

## 📋 Índice

1. [Arquitectura del Sistema](#arquitectura-del-sistema)
2. [Patrones de Diseño GoF](#patrones-de-diseño-gof)
3. [Capa de Datos (DAOs)](#capa-de-datos-daos)
4. [Capa de Servicios](#capa-de-servicios)
5. [Sistema de Notificaciones](#sistema-de-notificaciones)
6. [Sistema de Exportación](#sistema-de-exportación)
7. [Base de Datos](#base-de-datos)

---

## 1. Arquitectura del Sistema

### Arquitectura en Capas

El sistema sigue una **arquitectura en capas** (Layered Architecture) que separa las responsabilidades:

```
┌─────────────────────────────────────┐
│     Capa de Presentación (UI)      │
│   (Swing con FlatLaf Look & Feel)  │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│      Capa de Servicios (Facade)    │
│  TareaService, UsuarioService, etc │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│    Capa de Acceso a Datos (DAO)    │
│  TareaDAO, UsuarioDAO, EquipoDAO   │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│         Base de Datos               │
│        PostgreSQL 12+               │
└─────────────────────────────────────┘
```

### Componentes Principales

#### 1.1 Modelos de Dominio
- **Tarea**: Representa una tarea del sistema
- **Usuario**: Representa un usuario con credenciales
- **Equipo**: Representa un equipo de trabajo
- **Adjunto**: Metadatos de archivos adjuntos
- **Notificacion**: Notificaciones del sistema

#### 1.2 Capa de Datos (DAO)
- **TareaDAO**: CRUD completo de tareas
- **UsuarioDAO**: Gestión de usuarios y autenticación
- **EquipoDAO**: Gestión de equipos y miembros
- **AdjuntoDAO**: Gestión de metadatos de adjuntos
- **NotificacionDAO**: Gestión de notificaciones

#### 1.3 Capa de Servicios
- **TareaService**: Orquestación de operaciones de tareas
- **UsuarioService**: Gestión de usuarios y seguridad
- **EquipoService**: Gestión de equipos
- **EmailService**: Envío de notificaciones por email
- **EmailAttachmentService**: Gestión de adjuntos por email

#### 1.4 Utilidades
- **Validator**: Validaciones centralizadas
- **PasswordHasher**: Hash de contraseñas con BCrypt
- **EmailConfig**: Configuración SMTP
- **EmailTemplates**: Templates HTML para emails

---

## 2. Patrones de Diseño GoF

### 2.1 Singleton Pattern

**Ubicación**: `Conexion.java`

**Propósito**: Garantizar una única instancia de conexión a la base de datos.

```java
public class Conexion {
    private static Conexion instancia;
    private Connection conectar;
    
    private Conexion() { }
    
    public static Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }
}
```

**Ventajas**:
- Control centralizado de la conexión
- Evita múltiples conexiones innecesarias
- Facilita el manejo de recursos

---

### 2.2 Builder Pattern

**Ubicación**: `Tarea.java`

**Propósito**: Construcción flexible de objetos Tarea con múltiples parámetros opcionales.

```java
Tarea tarea = new Tarea.Builder("Título", idCreador)
    .descripcion("Descripción detallada")
    .fechaLimite(fecha)
    .idPrioridad(2)
    .idEstado(1)
    .build();
```

**Ventajas**:
- Código más legible
- Parámetros opcionales claros
- Inmutabilidad del objeto construido

---

### 2.3 Observer Pattern

**Ubicación**: `TaskScheduler.java`, `EmailService.java`, `NotificationService.java`

**Propósito**: Notificar automáticamente cuando las tareas están próximas a vencer.

```java
// Interfaz Observer
public interface TaskObserver {
    void onTaskDue(List<Tarea> tareas);
}

// Observador Concreto
public class EmailService implements TaskObserver {
    @Override
    public void onTaskDue(List<Tarea> tareas) {
        // Enviar emails de notificación
    }
}

// Subject
public class TaskScheduler {
    private List<TaskObserver> observers = new ArrayList<>();
    
    public void notifyObservers(List<Tarea> tasks) {
        for (TaskObserver observer : observers) {
            observer.onTaskDue(tasks);
        }
    }
}
```

**Ventajas**:
- Desacoplamiento entre scheduler y servicios de notificación
- Fácil agregar nuevos tipos de notificaciones
- Responsabilidad única

---

### 2.4 Strategy Pattern

**Ubicación**: `IReporteStrategy.java`, `PdfStrategy.java`, `ExcelStrategy.java`, `IcsStrategy.java`

**Propósito**: Permitir diferentes estrategias de exportación intercambiables.

```java
// Interfaz Strategy
public interface IReporteStrategy {
    boolean generar(List<Tarea> tareas);
}

// Estrategias Concretas
public class PdfStrategy implements IReporteStrategy { }
public class ExcelStrategy implements IReporteStrategy { }
public class IcsStrategy implements IReporteStrategy { }

// Uso
IReporteStrategy strategy = new PdfStrategy();
strategy.generar(tareas);
```

**Ventajas**:
- Fácil agregar nuevos formatos de exportación
- Código más mantenible
- Principio Open/Closed

---

### 2.5 Facade Pattern

**Ubicación**: `TareaService.java`, `UsuarioService.java`, `EquipoService.java`

**Propósito**: Simplificar operaciones complejas que involucran múltiples DAOs.

```java
public class TareaService {
    private TareaDAO tareaDAO;
    private NotificacionDAO notificacionDAO;
    private AdjuntoDAO adjuntoDAO;
    
    public boolean crearTareaCompleta(Tarea tarea, Integer idUsuario, Integer idEquipo) {
        // 1. Crear tarea
        tareaDAO.crearTarea(tarea, idUsuario, idEquipo);
        
        // 2. Crear notificación
        notificacionDAO.crearNotificacion(...);
        
        // 3. Procesar adjuntos
        adjuntoDAO.agregarAdjunto(...);
        
        return true;
    }
}
```

**Ventajas**:
- Interfaz simplificada para operaciones complejas
- Reduce acoplamiento entre capas
- Centraliza lógica de negocio

---

## 3. Capa de Datos (DAOs)

### 3.1 TareaDAO

**Responsabilidades**:
- CRUD completo de tareas
- Búsqueda y filtrado
- Gestión de asignaciones
- Archivado de tareas

**Métodos Principales**:
```java
// CRUD
boolean crearTarea(Tarea tarea, Integer idUsuario, Integer idEquipo)
Tarea getTareaPorId(int idTarea)
boolean actualizarTarea(Tarea tarea)
boolean eliminarTarea(int idTarea)

// Búsqueda y Filtros
List<Tarea> buscarTareas(String criterio)
List<Tarea> getTareasPorEstado(int idEstado)
List<Tarea> getTareasPorPrioridad(int idPrioridad)
List<Tarea> getTareasArchivadas(int idUsuario)

// Asignaciones
boolean reasignarTarea(int idTarea, Integer nuevoIdUsuario, Integer nuevoIdEquipo)
List<Usuario> getUsuariosAsignadosPorTarea(int idTarea)
List<Equipo> getEquiposAsignadosPorTarea(int idTarea)
```

---

### 3.2 UsuarioDAO

**Responsabilidades**:
- Gestión de usuarios
- Autenticación
- Gestión de roles
- Cambio de contraseñas

**Métodos Principales**:
```java
// CRUD
boolean crearUsuario(Usuario usuario, String password, int idRol)
Usuario getUsuarioPorId(int idUsuario)
boolean actualizarUsuario(Usuario usuario)
boolean eliminarUsuario(int idUsuario)

// Autenticación
Usuario login(String email, String password)
boolean validarCredenciales(String email, String password)

// Gestión de Contraseñas
boolean cambiarPassword(int idUsuario, String oldPassword, String newPassword)

// Búsqueda
List<Usuario> buscarUsuarios(String criterio)
Usuario getUsuarioPorEmail(String email)
```

---

### 3.3 EquipoDAO

**Responsabilidades**:
- CRUD de equipos
- Gestión de miembros
- Gestión de líderes

**Métodos Principales**:
```java
// CRUD
boolean crearEquipo(Equipo equipo)
Equipo getEquipoPorId(int idEquipo)
boolean actualizarEquipo(Equipo equipo)
boolean eliminarEquipo(int idEquipo)

// Gestión de Miembros
boolean agregarMiembro(int idEquipo, int idUsuario)
boolean removerMiembro(int idEquipo, int idUsuario)
List<Usuario> getMiembros(int idEquipo)
boolean esMiembro(int idEquipo, int idUsuario)

// Gestión de Líderes
boolean cambiarLider(int idEquipo, int nuevoIdLider)
```

---

## 4. Capa de Servicios

### 4.1 TareaService (Facade)

**Propósito**: Orquestar operaciones complejas de tareas que involucran múltiples DAOs.

**Operaciones Principales**:

#### Crear Tarea Completa
```java
public boolean crearTareaCompleta(Tarea tarea, Integer idUsuario, Integer idEquipo) {
    // 1. Crear tarea en BD
    // 2. Asignar a usuario/equipo
    // 3. Crear notificación
    // 4. Enviar email (opcional)
}
```

#### Cambiar Estado con Notificación
```java
public boolean cambiarEstadoTarea(int idTarea, int nuevoEstado) {
    // 1. Actualizar estado
    // 2. Obtener usuarios asignados
    // 3. Notificar a cada usuario
}
```

---

### 4.2 UsuarioService (Facade)

**Propósito**: Gestionar usuarios con validaciones y seguridad.

**Validaciones Implementadas**:
- Email válido (regex)
- Contraseña mínimo 6 caracteres
- Email único en el sistema
- Código de empleado único

---

### 4.3 EquipoService (Facade)

**Propósito**: Gestionar equipos y sus miembros.

**Reglas de Negocio**:
- Un equipo debe tener un líder
- El líder no puede ser removido del equipo
- Al cambiar líder, el nuevo debe ser miembro

---

## 5. Sistema de Notificaciones

### 5.1 EmailService

**Tecnología**: JavaMail API

**Funcionalidades**:
- Envío de emails HTML
- Soporte para adjuntos (hasta 25 MB)
- Templates profesionales
- Integración con Gmail, Outlook, Yahoo

**Templates Disponibles**:
1. **Asignación de Tarea**: Con información de adjuntos
2. **Vencimiento Próximo**: Con horas restantes
3. **Cambio de Estado**: Con colores según estado
4. **Tarea Completada**: Felicitación

---

### 5.2 EmailAttachmentService

**Propósito**: Gestionar archivos adjuntos enviados por email.

**Flujo**:
1. Validar archivos (tamaño, extensión)
2. Guardar metadatos en BD
3. Enviar archivos por email
4. Mostrar resumen en UI

**Validaciones**:
- Tamaño máximo: 25 MB total
- Extensiones permitidas: pdf, doc, docx, xls, xlsx, jpg, png, etc.

---

## 6. Sistema de Exportación

### 6.1 PdfStrategy (iText 7)

**Características**:
- Tabla profesional con 5 columnas
- Colores por estado y prioridad
- Encabezados con fondo gris
- Pie de página con marca del sistema

---

### 6.2 ExcelStrategy (Apache POI)

**Características**:
- Hoja de cálculo con 8 columnas
- 6 estilos personalizados
- Auto-ajuste de columnas
- Formato de fechas

---

### 6.3 IcsStrategy

**Características**:
- Formato iCalendar estándar (RFC 5545)
- Compatible con Google Calendar, Outlook
- Eventos con fecha límite

---

## 7. Base de Datos

### 7.1 Esquema

**Tablas Principales**:
- `usuarios`: Información de usuarios
- `credenciales`: Contraseñas y roles
- `tareas`: Tareas del sistema
- `equipos`: Equipos de trabajo
- `equipo_miembros`: Relación N:M
- `asignaciones_usuario`: Asignación de tareas a usuarios
- `asignaciones_equipo`: Asignación de tareas a equipos
- `adjuntos`: Metadatos de archivos
- `notificaciones`: Notificaciones del sistema

### 7.2 Índices

**Optimizaciones**:
```sql
CREATE INDEX idx_tareas_fecha_limite ON tareas(fecha_limite);
CREATE INDEX idx_tareas_estado ON tareas(id_estado);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_notificaciones_usuario ON notificaciones(id_usuario);
```

---

## 8. Seguridad

### 8.1 Contraseñas

**Tecnología**: BCrypt (jbcrypt-0.4)

**Implementación**:
```java
// Hash
String hashedPassword = PasswordHasher.hashPassword(plainPassword);

// Verificación
boolean isValid = PasswordHasher.verifyPassword(plainPassword, hashedPassword);
```

**Configuración**:
- Work factor: 12 (2^12 = 4096 iteraciones)
- Salt automático por BCrypt

---

### 8.2 Validaciones

**Validator.java** proporciona:
- Validación de email (regex)
- Validación de contraseñas (mínimo 6 caracteres, letra + número)
- Validación de archivos (tamaño, extensión)
- Sanitización de inputs (prevención SQL injection)

---

## 9. Dependencias

### Librerías Principales

| Librería | Versión | Propósito |
|----------|---------|-----------|
| PostgreSQL JDBC | 42.7.7 | Conexión a BD |
| JavaMail | 1.6.2 | Envío de emails |
| BCrypt | 0.4 | Hash de contraseñas |
| iText 7 | 7.2.5 | Exportación PDF |
| Apache POI | 5.2.5 | Exportación Excel |
| FlatLaf | 3.4.1 | Look & Feel UI |

---

## 10. Configuración

### 10.1 Base de Datos

**Archivo**: `Conexion.java`

```java
private static final String CADENA = "jdbc:postgresql://localhost:5432/tareas_db";
private static final String USUARIO = "postgres";
private static final String CONTRASENA = "password";
```

### 10.2 Email

**Archivo**: `EmailConfig.java`

```java
private static final String SMTP_HOST = "smtp.gmail.com";
private static final String SMTP_PORT = "587";
private static final String USERNAME = "tu-email@gmail.com";
private static final String PASSWORD = "tu-app-password";
```

---

## 11. Conclusión

El sistema implementa una arquitectura robusta y escalable utilizando:
- ✅ 5 Patrones de Diseño GoF
- ✅ Arquitectura en capas
- ✅ Separación de responsabilidades
- ✅ Código mantenible y extensible
- ✅ Seguridad con BCrypt
- ✅ Validaciones robustas
- ✅ Sistema de notificaciones completo
- ✅ Múltiples formatos de exportación
