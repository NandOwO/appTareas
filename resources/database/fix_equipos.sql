-- =====================================================
-- DIAGNÓSTICO Y CORRECCIÓN DE EQUIPOS
-- =====================================================

-- 1. VERIFICAR SI HAY EQUIPOS
SELECT COUNT(*) as total_equipos FROM equipos;

-- 2. VER EQUIPOS CON SUS LÍDERES
SELECT 
    e.id_equipo, 
    e.nombre as nombre_equipo, 
    e.id_lider, 
    u.nombre as nombre_lider,
    e.activo
FROM equipos e
LEFT JOIN usuarios u ON e.id_lider = u.id_usuario;

-- 3. VER USUARIOS DISPONIBLES PARA SER LÍDERES
SELECT id_usuario, nombre, email 
FROM usuarios 
WHERE activo = TRUE
ORDER BY id_usuario;

-- =====================================================
-- SOLUCIÓN: INSERTAR EQUIPOS SI NO EXISTEN
-- =====================================================

-- Eliminar equipos existentes (opcional, solo si quieres empezar de cero)
-- DELETE FROM equipo_miembros;
-- DELETE FROM equipos;

-- Insertar Equipo Desarrollo (líder: primer usuario activo)
INSERT INTO equipos (nombre, descripcion, id_lider, activo)
SELECT 'Equipo Desarrollo', 'Equipo de desarrollo de software', id_usuario, TRUE
FROM usuarios 
WHERE activo = TRUE 
LIMIT 1
ON CONFLICT DO NOTHING;

-- Insertar Equipo Diseño (líder: segundo usuario activo)
INSERT INTO equipos (nombre, descripcion, id_lider, activo)
SELECT 'Equipo Diseño', 'Equipo de diseño y UX', id_usuario, TRUE
FROM usuarios 
WHERE activo = TRUE 
OFFSET 1 LIMIT 1
ON CONFLICT DO NOTHING;

-- =====================================================
-- VERIFICAR INSERCIÓN
-- =====================================================

-- Ver equipos insertados
SELECT 
    e.id_equipo, 
    e.nombre, 
    u.nombre as lider,
    (SELECT COUNT(*) FROM equipo_miembros em WHERE em.id_equipo = e.id_equipo) as num_miembros
FROM equipos e
JOIN usuarios u ON e.id_lider = u.id_usuario
WHERE e.activo = TRUE;

-- =====================================================
-- AGREGAR MIEMBROS A LOS EQUIPOS (OPCIONAL)
-- =====================================================

-- Agregar miembros al Equipo Desarrollo
INSERT INTO equipo_miembros (id_equipo, id_usuario)
SELECT e.id_equipo, u.id_usuario
FROM equipos e, usuarios u
WHERE e.nombre = 'Equipo Desarrollo'
AND u.activo = TRUE
AND u.id_usuario != e.id_lider  -- No agregar al líder como miembro
LIMIT 2
ON CONFLICT DO NOTHING;

-- Agregar miembros al Equipo Diseño
INSERT INTO equipo_miembros (id_equipo, id_usuario)
SELECT e.id_equipo, u.id_usuario
FROM equipos e, usuarios u
WHERE e.nombre = 'Equipo Diseño'
AND u.activo = TRUE
AND u.id_usuario != e.id_lider
AND u.id_usuario NOT IN (
    SELECT id_usuario FROM equipo_miembros WHERE id_equipo = (SELECT id_equipo FROM equipos WHERE nombre = 'Equipo Desarrollo')
)
LIMIT 2
ON CONFLICT DO NOTHING;

-- =====================================================
-- VERIFICACIÓN FINAL
-- =====================================================

-- Resumen completo
SELECT 
    'Equipos' as tabla, 
    COUNT(*) as total 
FROM equipos
UNION ALL
SELECT 
    'Miembros de Equipos', 
    COUNT(*) 
FROM equipo_miembros;

-- Detalle de equipos con miembros
SELECT 
    e.nombre as equipo,
    u_lider.nombre as lider,
    COUNT(em.id_usuario) as num_miembros
FROM equipos e
JOIN usuarios u_lider ON e.id_lider = u_lider.id_usuario
LEFT JOIN equipo_miembros em ON e.id_equipo = em.id_equipo
WHERE e.activo = TRUE
GROUP BY e.id_equipo, e.nombre, u_lider.nombre;
