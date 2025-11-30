-- ============================================
-- SCRIPT DE INICIALIZACIÓN - Base de Datos Synapse
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

-- TABLA 6: ESTADOS DE TAREA
CREATE TABLE IF NOT EXISTS estados_tarea (
    id_estado SERIAL PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL UNIQUE
);

-- TABLA 7: PRIORIDADES
CREATE TABLE IF NOT EXISTS prioridades (
    id_prioridad SERIAL PRIMARY KEY,
    nombre_prioridad VARCHAR(50) NOT NULL UNIQUE
);

-- TABLA 8: TAREAS
CREATE TABLE IF NOT EXISTS tareas (
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

-- TABLA 9: ASIGNACIONES DE TAREAS A USUARIOS
CREATE TABLE IF NOT EXISTS tarea_usuario (
    id_tarea INT,
    id_usuario INT,
    PRIMARY KEY (id_tarea, id_usuario),
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- TABLA 10: ASIGNACIONES DE TAREAS A EQUIPOS
CREATE TABLE IF NOT EXISTS tarea_equipo (
    id_tarea INT,
    id_equipo INT,
    PRIMARY KEY (id_tarea, id_equipo),
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE,
    FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo) ON DELETE CASCADE
);

-- TABLA 11: ADJUNTOS (si se usa)
CREATE TABLE IF NOT EXISTS adjuntos (
    id_adjunto SERIAL PRIMARY KEY,
    id_tarea INT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_archivo VARCHAR(50),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea) ON DELETE CASCADE
);

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Insertar roles
INSERT INTO roles (nombre_rol) VALUES 
    ('Administrador'),
    ('Gerente'),
    ('Empleado')
ON CONFLICT (nombre_rol) DO NOTHING;

-- Insertar estados de tarea
INSERT INTO estados_tarea (nombre_estado) VALUES 
    ('Pendiente'),
    ('En Progreso'),
    ('Completada'),
    ('Pausada')
ON CONFLICT (nombre_estado) DO NOTHING;

-- Insertar prioridades
INSERT INTO prioridades (nombre_prioridad) VALUES 
    ('Baja'),
    ('Media'),
    ('Alta')
ON CONFLICT (nombre_prioridad) DO NOTHING;

-- ============================================
-- USUARIO ADMINISTRADOR
-- ============================================

-- Insertar usuario admin
INSERT INTO usuarios (nombre, email, activo, codigo_empleado) 
VALUES ('Administrador', 'admin@synapse.com', TRUE, 'ADMIN001')
ON CONFLICT (email) DO NOTHING;

-- Insertar credenciales del admin
-- Password: admin123 (hash BCrypt)
INSERT INTO credenciales (id_usuario, password, id_rol)
SELECT 
    u.id_usuario,
    '$2a$10$9RbTaAhwsW92zYdEbDlEhOLubGAKRTXJjxz1QOOTJ8ELHbJeoh4iDS', -- admin123
    1 -- Rol Administrador
FROM usuarios u
WHERE u.email = 'admin@synapse.com'
ON CONFLICT DO NOTHING;

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE 'Base de datos inicializada correctamente';
    RAISE NOTICE 'Usuario: admin@synapse.com';
    RAISE NOTICE 'Contraseña: admin123';
END $$;
