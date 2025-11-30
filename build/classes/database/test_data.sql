-- Script de Datos de Prueba para Sistema de Gestión de Tareas
-- Ejecutar después de crear el esquema completo

-- =====================================================
-- 1. LIMPIAR DATOS EXISTENTES (OPCIONAL)
-- =====================================================
-- TRUNCATE TABLE notificaciones CASCADE;
-- TRUNCATE TABLE adjuntos CASCADE;
-- TRUNCATE TABLE asignaciones_equipo CASCADE;
-- TRUNCATE TABLE asignaciones_usuario CASCADE;
-- TRUNCATE TABLE tareas CASCADE;
-- TRUNCATE TABLE equipo_miembros CASCADE;
-- TRUNCATE TABLE equipos CASCADE;
-- TRUNCATE TABLE credenciales CASCADE;
-- TRUNCATE TABLE usuarios CASCADE;

-- =====================================================
-- 2. INSERTAR USUARIOS DE PRUEBA
-- =====================================================

-- Usuario Administrador
INSERT INTO usuarios (nombre, email, codigo_empleado, activo)
VALUES ('Admin Sistema', 'admin@synapse.com', 'ADM001', TRUE);

-- Usuarios Gerentes
INSERT INTO usuarios (nombre, email, codigo_empleado, activo)
VALUES 
('Juan Pérez', 'juan.perez@synapse.com', 'GER001', TRUE),
('María García', 'maria.garcia@synapse.com', 'GER002', TRUE);

-- Usuarios Empleados
INSERT INTO usuarios (nombre, email, codigo_empleado, activo)
VALUES 
('Carlos López', 'carlos.lopez@synapse.com', 'EMP001', TRUE),
('Ana Martínez', 'ana.martinez@synapse.com', 'EMP002', TRUE),
('Luis Rodríguez', 'luis.rodriguez@synapse.com', 'EMP003', TRUE),
('Elena Fernández', 'elena.fernandez@synapse.com', 'EMP004', TRUE);

-- =====================================================
-- 3. INSERTAR CREDENCIALES
-- =====================================================
-- Password para todos: "test123"
-- Hash BCrypt: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.ckuQwC

-- Admin (id_rol = 1)
INSERT INTO credenciales (id_usuario, password, id_rol)
SELECT id_usuario, '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.ckuQwC', 1
FROM usuarios WHERE email = 'admin@synapse.com';

-- Gerentes (id_rol = 2)
INSERT INTO credenciales (id_usuario, password, id_rol)
SELECT id_usuario, '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.ckuQwC', 2
FROM usuarios WHERE email IN ('juan.perez@synapse.com', 'maria.garcia@synapse.com');

-- Empleados (id_rol = 3)
INSERT INTO credenciales (id_usuario, password, id_rol)
SELECT id_usuario, '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.ckuQwC', 3
FROM usuarios WHERE email IN (
    'carlos.lopez@synapse.com',
    'ana.martinez@synapse.com',
    'luis.rodriguez@synapse.com',
    'elena.fernandez@synapse.com'
);

-- =====================================================
-- 4. INSERTAR EQUIPOS
-- =====================================================

-- Equipo de Desarrollo (Líder: Juan Pérez)
INSERT INTO equipos (nombre, descripcion, id_lider, activo)
SELECT 'Equipo Desarrollo', 'Equipo de desarrollo de software', id_usuario, TRUE
FROM usuarios WHERE email = 'juan.perez@synapse.com';

-- Equipo de Diseño (Líder: María García)
INSERT INTO equipos (nombre, descripcion, id_lider, activo)
SELECT 'Equipo Diseño', 'Equipo de diseño y UX', id_usuario, TRUE
FROM usuarios WHERE email = 'maria.garcia@synapse.com';

-- =====================================================
-- 5. AGREGAR MIEMBROS A EQUIPOS
-- =====================================================

-- Miembros del Equipo Desarrollo
INSERT INTO equipo_miembros (id_equipo, id_usuario)
SELECT e.id_equipo, u.id_usuario
FROM equipos e, usuarios u
WHERE e.nombre = 'Equipo Desarrollo'
AND u.email IN ('carlos.lopez@synapse.com', 'luis.rodriguez@synapse.com');

-- Miembros del Equipo Diseño
INSERT INTO equipo_miembros (id_equipo, id_usuario)
SELECT e.id_equipo, u.id_usuario
FROM equipos e, usuarios u
WHERE e.nombre = 'Equipo Diseño'
AND u.email IN ('ana.martinez@synapse.com', 'elena.fernandez@synapse.com');

-- =====================================================
-- 6. INSERTAR TAREAS DE PRUEBA
-- =====================================================

-- Tarea 1: Alta Prioridad, Pendiente
INSERT INTO tareas (titulo, descripcion, fecha_creacion, fecha_limite, id_estado, id_prioridad, creada_por, archivada)
SELECT 
    'Implementar módulo de autenticación',
    'Desarrollar el sistema de login con JWT y refresh tokens',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    1, -- Pendiente
    3, -- Alta
    id_usuario,
    FALSE
FROM usuarios WHERE email = 'juan.perez@synapse.com';

-- Tarea 2: Media Prioridad, En Progreso
INSERT INTO tareas (titulo, descripcion, fecha_creacion, fecha_limite, id_estado, id_prioridad, creada_por, archivada)
SELECT 
    'Diseñar interfaz de dashboard',
    'Crear mockups y prototipos del dashboard principal',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '5 days',
    2, -- En Progreso
    2, -- Media
    id_usuario,
    FALSE
FROM usuarios WHERE email = 'maria.garcia@synapse.com';

-- Tarea 3: Baja Prioridad, Completada
INSERT INTO tareas (titulo, descripcion, fecha_creacion, fecha_limite, id_estado, id_prioridad, creada_por, archivada)
SELECT 
    'Documentar API REST',
    'Crear documentación completa de todos los endpoints',
    CURRENT_TIMESTAMP - INTERVAL '10 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    3, -- Completada
    1, -- Baja
    id_usuario,
    FALSE
FROM usuarios WHERE email = 'juan.perez@synapse.com';

-- Tarea 4: Alta Prioridad, Pendiente (próxima a vencer)
INSERT INTO tareas (titulo, descripcion, fecha_creacion, fecha_limite, id_estado, id_prioridad, creada_por, archivada)
SELECT 
    'Corregir bugs críticos',
    'Resolver los 5 bugs críticos reportados en producción',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '1 day',
    1, -- Pendiente
    3, -- Alta
    id_usuario,
    FALSE
FROM usuarios WHERE email = 'admin@synapse.com';

-- Tarea 5: Media Prioridad, En Progreso
INSERT INTO tareas (titulo, descripcion, fecha_creacion, fecha_limite, id_estado, id_prioridad, creada_por, archivada)
SELECT 
    'Optimizar consultas de base de datos',
    'Mejorar el rendimiento de las consultas más lentas',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '14 days',
    2, -- En Progreso
    2, -- Media
    id_usuario,
    FALSE
FROM usuarios WHERE email = 'juan.perez@synapse.com';

-- =====================================================
-- 7. ASIGNAR TAREAS A USUARIOS
-- =====================================================

-- Tarea 1 → Carlos López
INSERT INTO asignaciones_usuario (id_tarea, id_usuario)
SELECT t.id_tarea, u.id_usuario
FROM tareas t, usuarios u
WHERE t.titulo = 'Implementar módulo de autenticación'
AND u.email = 'carlos.lopez@synapse.com';

-- Tarea 2 → Ana Martínez
INSERT INTO asignaciones_usuario (id_tarea, id_usuario)
SELECT t.id_tarea, u.id_usuario
FROM tareas t, usuarios u
WHERE t.titulo = 'Diseñar interfaz de dashboard'
AND u.email = 'ana.martinez@synapse.com';

-- Tarea 4 → Luis Rodríguez
INSERT INTO asignaciones_usuario (id_tarea, id_usuario)
SELECT t.id_tarea, u.id_usuario
FROM tareas t, usuarios u
WHERE t.titulo = 'Corregir bugs críticos'
AND u.email = 'luis.rodriguez@synapse.com';

-- =====================================================
-- 8. ASIGNAR TAREAS A EQUIPOS
-- =====================================================

-- Tarea 5 → Equipo Desarrollo
INSERT INTO asignaciones_equipo (id_tarea, id_equipo)
SELECT t.id_tarea, e.id_equipo
FROM tareas t, equipos e
WHERE t.titulo = 'Optimizar consultas de base de datos'
AND e.nombre = 'Equipo Desarrollo';

-- =====================================================
-- 9. INSERTAR NOTIFICACIONES
-- =====================================================

-- Notificación para Carlos López
INSERT INTO notificaciones (id_usuario, id_tarea, mensaje, fecha, leida)
SELECT u.id_usuario, t.id_tarea, 
       'Se te ha asignado una nueva tarea: Implementar módulo de autenticación',
       CURRENT_TIMESTAMP,
       FALSE
FROM usuarios u, tareas t
WHERE u.email = 'carlos.lopez@synapse.com'
AND t.titulo = 'Implementar módulo de autenticación';

-- Notificación para Ana Martínez
INSERT INTO notificaciones (id_usuario, id_tarea, mensaje, fecha, leida)
SELECT u.id_usuario, t.id_tarea,
       'Se te ha asignado una nueva tarea: Diseñar interfaz de dashboard',
       CURRENT_TIMESTAMP,
       FALSE
FROM usuarios u, tareas t
WHERE u.email = 'ana.martinez@synapse.com'
AND t.titulo = 'Diseñar interfaz de dashboard';

-- Notificación de tarea completada
INSERT INTO notificaciones (id_usuario, id_tarea, mensaje, fecha, leida)
SELECT u.id_usuario, t.id_tarea,
       '¡Felicidades! Has completado la tarea: Documentar API REST',
       CURRENT_TIMESTAMP - INTERVAL '2 days',
       TRUE
FROM usuarios u, tareas t
WHERE u.email = 'juan.perez@synapse.com'
AND t.titulo = 'Documentar API REST';

-- =====================================================
-- 10. VERIFICAR DATOS INSERTADOS
-- =====================================================

-- Contar registros
SELECT 'Usuarios' as tabla, COUNT(*) as total FROM usuarios
UNION ALL
SELECT 'Credenciales', COUNT(*) FROM credenciales
UNION ALL
SELECT 'Equipos', COUNT(*) FROM equipos
UNION ALL
SELECT 'Miembros de Equipos', COUNT(*) FROM equipo_miembros
UNION ALL
SELECT 'Tareas', COUNT(*) FROM tareas
UNION ALL
SELECT 'Asignaciones Usuario', COUNT(*) FROM asignaciones_usuario
UNION ALL
SELECT 'Asignaciones Equipo', COUNT(*) FROM asignaciones_equipo
UNION ALL
SELECT 'Notificaciones', COUNT(*) FROM notificaciones;

-- Mostrar usuarios con sus roles
SELECT u.nombre, u.email, r.nombre_rol
FROM usuarios u
JOIN credenciales c ON u.id_usuario = c.id_usuario
JOIN roles r ON c.id_rol = r.id_rol
ORDER BY r.id_rol, u.nombre;

-- Mostrar tareas con asignaciones
SELECT 
    t.titulo,
    t.fecha_limite,
    e.nombre_estado as estado,
    p.nombre_prioridad as prioridad,
    COALESCE(u.nombre, eq.nombre) as asignado_a
FROM tareas t
JOIN estados_tarea e ON t.id_estado = e.id_estado
JOIN prioridades_tarea p ON t.id_prioridad = p.id_prioridad
LEFT JOIN asignaciones_usuario au ON t.id_tarea = au.id_tarea
LEFT JOIN usuarios u ON au.id_usuario = u.id_usuario
LEFT JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea
LEFT JOIN equipos eq ON ae.id_equipo = eq.id_equipo
ORDER BY t.fecha_limite;

-- =====================================================
-- RESUMEN DE DATOS DE PRUEBA
-- =====================================================
-- 
-- Usuarios creados: 7
--   - 1 Administrador (admin@synapse.com)
--   - 2 Gerentes (juan.perez, maria.garcia)
--   - 4 Empleados (carlos, ana, luis, elena)
--
-- Password para todos: "test123"
--
-- Equipos: 2
--   - Equipo Desarrollo (3 miembros)
--   - Equipo Diseño (3 miembros)
--
-- Tareas: 5
--   - 2 Pendientes (1 próxima a vencer)
--   - 2 En Progreso
--   - 1 Completada
--
-- Notificaciones: 3
--   - 2 No leídas
--   - 1 Leída
-- =====================================================
