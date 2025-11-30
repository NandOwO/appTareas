-- ============================================
-- SCRIPT BASE DE DATOS: Esquema completo
-- ============================================

-- TABLA 1: ROLES
CREATE TABLE IF NOT EXISTS roles (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);

-- TABLA 2: USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    foto_url VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    codigo_empleado VARCHAR(50) UNIQUE
);

-- TABLA 3: CREDENCIALES
CREATE TABLE IF NOT EXISTS credenciales (
    id_credencial SERIAL PRIMARY KEY,
    id_usuario INT REFERENCES usuarios(id_usuario),
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- TABLA 4: EQUIPOS
CREATE TABLE IF NOT EXISTS equipos (
    id_equipo SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_lider INT,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_lider) REFERENCES usuarios(id_usuario)
);

-- TABLA 5: MIEMBROS DE EQUIPO
CREATE TABLE IF NOT EXISTS equipo_miembros (
    id_equipo INT,
    id_usuario INT,
    PRIMARY KEY (id_equipo, id_usuario),
    FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- TABLAS 6 y 7: ESTADOS y PRIORIDADES
CREATE TABLE IF NOT EXISTS estados_tarea (
    id_estado SERIAL PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS prioridades_tarea (
    id_prioridad SERIAL PRIMARY KEY,
    nombre_prioridad VARCHAR(50) NOT NULL UNIQUE
);

-- TABLA 8: TAREAS
CREATE TABLE IF NOT EXISTS tareas (
    id_tarea SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_limite TIMESTAMP,
    creada_por INT NOT NULL,
    id_estado INT NOT NULL,
    id_prioridad INT NOT NULL,
    archivada BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (creada_por) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_estado) REFERENCES estados_tarea(id_estado),
    FOREIGN KEY (id_prioridad) REFERENCES prioridades_tarea(id_prioridad)
);

-- TABLAS 9 y 10: ASIGNACIONES
CREATE TABLE IF NOT EXISTS asignaciones_usuario (
    id_asignacion_usuario SERIAL PRIMARY KEY,
    id_tarea INT NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    UNIQUE (id_tarea, id_usuario)
);

CREATE TABLE IF NOT EXISTS asignaciones_equipo (
    id_asignacion_equipo SERIAL PRIMARY KEY,
    id_tarea INT NOT NULL,
    id_equipo INT NOT NULL,
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE,
    FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo) ON DELETE CASCADE,
    UNIQUE (id_tarea, id_equipo)
);

-- TABLA 11: ADJUNTOS
CREATE TABLE IF NOT EXISTS adjuntos (
    id_adjunto SERIAL PRIMARY KEY,
    id_tarea INT NOT NULL,
    nombre_archivo VARCHAR(255),
    ruta_archivo TEXT,
    tipo_archivo VARCHAR(20),
    tamanio_bytes BIGINT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE
);

-- TABLA 12: NOTIFICACIONES
CREATE TABLE IF NOT EXISTS notificaciones (
    id_notificacion SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tarea INT NOT NULL,
    mensaje TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leida BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea)
);

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Roles de usuario
INSERT INTO roles (nombre_rol) VALUES
('administrador'),
('gerente'),
('empleado')
ON CONFLICT (nombre_rol) DO NOTHING;

-- Estados de tarea
INSERT INTO estados_tarea (nombre_estado) VALUES
('pendiente'),
('en_progreso'),
('completada')
ON CONFLICT (nombre_estado) DO NOTHING;

-- Prioridades de tarea
INSERT INTO prioridades_tarea (nombre_prioridad) VALUES
('baja'),
('media'),
('alta')
ON CONFLICT (nombre_prioridad) DO NOTHING;

-- ============================================
-- ÍNDICES PARA RENDIMIENTO
-- ============================================

CREATE INDEX IF NOT EXISTS idx_tareas_estado ON tareas(id_estado);
CREATE INDEX IF NOT EXISTS idx_tareas_prioridad ON tareas(id_prioridad);
CREATE INDEX IF NOT EXISTS idx_tareas_fecha_limite ON tareas(fecha_limite);
CREATE INDEX IF NOT EXISTS idx_tareas_archivada ON tareas(archivada);
CREATE INDEX IF NOT EXISTS idx_asignaciones_usuario_tarea ON asignaciones_usuario(id_tarea);
CREATE INDEX IF NOT EXISTS idx_asignaciones_usuario_usuario ON asignaciones_usuario(id_usuario);
CREATE INDEX IF NOT EXISTS idx_asignaciones_equipo_tarea ON asignaciones_equipo(id_tarea);
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario ON notificaciones(id_usuario);
CREATE INDEX IF NOT EXISTS idx_notificaciones_leida ON notificaciones(leida);
