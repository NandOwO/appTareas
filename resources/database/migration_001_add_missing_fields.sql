-- ============================================
-- SCRIPT DE MIGRACIÓN: Agregar campos faltantes
-- ============================================

-- 1. Agregar campo 'archivada' a tareas
ALTER TABLE tareas ADD COLUMN IF NOT EXISTS archivada BOOLEAN DEFAULT FALSE;

-- 2. Agregar campo 'activo' a equipos (para soft delete)
ALTER TABLE equipos ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

-- 3. Agregar campo 'id_lider' a equipos (faltaba en tu schema)
ALTER TABLE equipos ADD COLUMN IF NOT EXISTS id_lider INT;
ALTER TABLE equipos ADD CONSTRAINT fk_equipo_lider 
    FOREIGN KEY (id_lider) REFERENCES usuarios(id_usuario);

-- 4. Agregar campo 'codigo_empleado' a usuarios (usado en el modelo Java)
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS codigo_empleado VARCHAR(50) UNIQUE;

-- 5. Mejorar tabla adjuntos con metadatos
ALTER TABLE adjuntos ADD COLUMN IF NOT EXISTS tipo_archivo VARCHAR(20);
ALTER TABLE adjuntos ADD COLUMN IF NOT EXISTS tamanio_bytes BIGINT;
ALTER TABLE adjuntos ADD COLUMN IF NOT EXISTS fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 6. Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_tareas_estado ON tareas(id_estado);
CREATE INDEX IF NOT EXISTS idx_tareas_prioridad ON tareas(id_prioridad);
CREATE INDEX IF NOT EXISTS idx_tareas_fecha_limite ON tareas(fecha_limite);
CREATE INDEX IF NOT EXISTS idx_tareas_archivada ON tareas(archivada);
CREATE INDEX IF NOT EXISTS idx_asignaciones_usuario_tarea ON asignaciones_usuario(id_tarea);
CREATE INDEX IF NOT EXISTS idx_asignaciones_usuario_usuario ON asignaciones_usuario(id_usuario);
CREATE INDEX IF NOT EXISTS idx_asignaciones_equipo_tarea ON asignaciones_equipo(id_tarea);
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario ON notificaciones(id_usuario);
CREATE INDEX IF NOT EXISTS idx_notificaciones_leida ON notificaciones(leida);

-- 7. Comentarios para documentación
COMMENT ON TABLE tareas IS 'Almacena todas las tareas del sistema';
COMMENT ON COLUMN tareas.archivada IS 'Indica si la tarea está archivada (soft delete)';
COMMENT ON TABLE adjuntos IS 'Metadatos de archivos adjuntos (archivos se envían por email)';
COMMENT ON COLUMN adjuntos.tipo_archivo IS 'Tipo: imagen, documento, etc.';
COMMENT ON COLUMN adjuntos.tamanio_bytes IS 'Tamaño del archivo en bytes';
