package com.synapse.data.dao;

import com.synapse.core.models.Equipo;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.data.database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FERNANDO
 */
public class TareaDAO {

    private final Conexion con = Conexion.getInstance();

    public boolean crearTarea(Tarea tarea, Integer idAsignadoUsuario, Integer idAsignadoEquipo) {
        String sqlTarea = "INSERT INTO tareas (titulo, descripcion, fecha_limite, creada_por, id_estado, id_prioridad, fecha_creacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlAsigUsuario = "INSERT INTO asignaciones_usuario (id_tarea, id_usuario) VALUES (?, ?)";
        String sqlAsigEquipo = "INSERT INTO asignaciones_equipo (id_tarea, id_equipo) VALUES (?, ?)";

        Connection cn = null;
        try {
            cn = con.getConnection();
            cn.setAutoCommit(false);

            // 1. Insertar Tarea
            int idTareaGenerada;
            try (PreparedStatement psTarea = cn.prepareStatement(sqlTarea, Statement.RETURN_GENERATED_KEYS)) {
                psTarea.setString(1, tarea.getTitulo());
                psTarea.setString(2, tarea.getDescripcion());
                psTarea.setTimestamp(3, tarea.getFechaLimite());
                psTarea.setInt(4, tarea.getCreadaPor());
                psTarea.setInt(5, tarea.getIdEstado());
                psTarea.setInt(6, tarea.getIdPrioridad());
                psTarea.setTimestamp(7, tarea.getFechaCreacion());
                psTarea.executeUpdate();

                try (ResultSet rs = psTarea.getGeneratedKeys()) {
                    if (rs.next()) {
                        idTareaGenerada = rs.getInt(1);
                        // *** FIX: Actualizar el objeto Tarea con el ID generado ***
                        tarea.setIdTarea(idTareaGenerada);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la tarea creada.");
                    }
                }
            }

            if (idAsignadoUsuario != null && idAsignadoUsuario > 0) {
                try (PreparedStatement psAsigUser = cn.prepareStatement(sqlAsigUsuario)) {
                    psAsigUser.setInt(1, idTareaGenerada);
                    psAsigUser.setInt(2, idAsignadoUsuario);
                    psAsigUser.executeUpdate();
                }
            }

            if (idAsignadoEquipo != null && idAsignadoEquipo > 0) {
                try (PreparedStatement psAsigTeam = cn.prepareStatement(sqlAsigEquipo)) {
                    psAsigTeam.setInt(1, idTareaGenerada);
                    psAsigTeam.setInt(2, idAsignadoEquipo);
                    psAsigTeam.executeUpdate();
                }
            }

            cn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (cn != null) {
                    System.err.println("Transacción fallida. Haciendo rollback...");
                    cn.rollback();
                }
            } catch (SQLException e2) {
                System.err.println("Error haciendo rollback: " + e2.getMessage());
            }
            return false;
        } finally {
            try {
                if (cn != null) {
                    cn.setAutoCommit(true);
                    cn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Tarea> getTareasPorUsuario(int idUsuario) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT DISTINCT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "LEFT JOIN asignaciones_usuario au ON t.id_tarea = au.id_tarea "
                + "LEFT JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea "
                + "LEFT JOIN equipo_miembros em ON ae.id_equipo = em.id_equipo "
                + "WHERE (au.id_usuario = ? OR em.id_usuario = ?) AND t.archivada = FALSE";

        try (Connection cn = con.getConnection(); // CORREGIDO
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    public List<Tarea> buscarTareasProximasAVencer(int horas) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "WHERE t.fecha_limite BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + (? || ' hours')::INTERVAL "
                + "AND t.id_estado != 3 AND t.archivada = FALSE"; // 3 = Completada

        try (Connection cn = con.getConnection(); // CORREGIDO
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, horas);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    public boolean actualizarEstadoTarea(int idTarea, int idNuevoEstado) {
        String sql = "UPDATE tareas SET id_estado = ? WHERE id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idNuevoEstado);
            ps.setInt(2, idTarea);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Tarea> getTareasPorUsuario(int idUsuario, java.util.Date fechaDesde, java.util.Date fechaHasta) {
        List<Tarea> tareas = new ArrayList<>();

        // Empezamos con el query base
        String sql = "SELECT DISTINCT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "LEFT JOIN asignaciones_usuario au ON t.id_tarea = au.id_tarea "
                + "LEFT JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea "
                + "LEFT JOIN equipo_miembros em ON ae.id_equipo = em.id_equipo "
                + "WHERE (au.id_usuario = ? OR em.id_usuario = ?) AND t.archivada = FALSE "; // Parámetros 1 y 2

        // Añadimos los filtros de fecha dinámicamente
        if (fechaDesde != null) {
            sql += " AND t.fecha_limite >= ? "; // Parámetro 3
        }
        if (fechaHasta != null) {
            sql += " AND t.fecha_limite <= ? "; // Parámetro 4 (o 3)
        }

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, idUsuario);
            ps.setInt(paramIndex++, idUsuario);

            if (fechaDesde != null) {
                ps.setTimestamp(paramIndex++, new java.sql.Timestamp(fechaDesde.getTime()));
            }
            if (fechaHasta != null) {
                ps.setTimestamp(paramIndex++, new java.sql.Timestamp(fechaHasta.getTime()));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs)); // Reutilizamos tu helper
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Obtiene las tareas asignadas a un equipo específico. Requerido por:
     * formMiEquipo.java
     */
    public List<Tarea> getTareasPorEquipo(int idEquipo) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea "
                + "WHERE ae.id_equipo = ? AND t.archivada = FALSE";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs)); // Reutilizamos el helper
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Obtiene TODAS las tareas. (Para Admin) Requerido por: formTareas.java
     */
    public List<Tarea> getAllTareas() {
        List<Tarea> tareas = new ArrayList<>();
        // Este query usa COALESCE para obtener el nombre del usuario o equipo asignado
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u_creador.nombre as nombre_creador, "
                + "COALESCE(asig_u.nombre, asig_e.nombre, 'N/A') AS nombre_asignado "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u_creador ON t.creada_por = u_creador.id_usuario "
                + "LEFT JOIN asignaciones_usuario au ON t.id_tarea = au.id_tarea "
                + "LEFT JOIN usuarios asig_u ON au.id_usuario = asig_u.id_usuario "
                + "LEFT JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea "
                + "LEFT JOIN equipos asig_e ON ae.id_equipo = asig_e.id_equipo "
                + "WHERE t.archivada = FALSE "
                + "ORDER BY t.fecha_limite DESC";

        try (Connection cn = con.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarea tarea = mapResultSetToTarea(rs);
                tarea.setNombreAsignado(rs.getString("nombre_asignado")); // <-- ¡Guardamos el nombre!
                tareas.add(tarea);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Obtiene tareas creadas por un Gerente. (Para Gerente) Requerido por:
     * formTareas.java
     */
    public List<Tarea> getTareasPorGerente(int idGerente) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u_creador.nombre as nombre_creador, "
                + "COALESCE(asig_u.nombre, asig_e.nombre, 'N/A') AS nombre_asignado "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u_creador ON t.creada_por = u_creador.id_usuario "
                + "LEFT JOIN asignaciones_usuario au ON t.id_tarea = au.id_tarea "
                + "LEFT JOIN usuarios asig_u ON au.id_usuario = asig_u.id_usuario "
                + "LEFT JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea "
                + "LEFT JOIN equipos asig_e ON ae.id_equipo = asig_e.id_equipo "
                + "WHERE t.creada_por = ? AND t.archivada = FALSE " // Filtro de Gerente
                + "ORDER BY t.fecha_limite DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idGerente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tarea tarea = mapResultSetToTarea(rs);
                    tarea.setNombreAsignado(rs.getString("nombre_asignado")); // <-- ¡Guardamos el nombre!
                    tareas.add(tarea);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Obtiene una tarea por su ID con todos los datos relacionados
     */
    public Tarea getTareaPorId(int idTarea) {
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador, "
                + "COALESCE(asig_u.nombre, asig_e.nombre, 'N/A') AS nombre_asignado "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "LEFT JOIN asignaciones_usuario au ON t.id_tarea = au.id_tarea "
                + "LEFT JOIN usuarios asig_u ON au.id_usuario = asig_u.id_usuario "
                + "LEFT JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea "
                + "LEFT JOIN equipos asig_e ON ae.id_equipo = asig_e.id_equipo "
                + "WHERE t.id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tarea tarea = mapResultSetToTarea(rs);
                    tarea.setNombreAsignado(rs.getString("nombre_asignado"));
                    return tarea;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza una tarea existente
     */
    public boolean actualizarTarea(Tarea tarea) {
        String sql = "UPDATE tareas SET titulo = ?, descripcion = ?, fecha_limite = ?, "
                + "id_estado = ?, id_prioridad = ? WHERE id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setTimestamp(3, tarea.getFechaLimite());
            ps.setInt(4, tarea.getIdEstado());
            ps.setInt(5, tarea.getIdPrioridad());
            ps.setInt(6, tarea.getIdTarea());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una tarea físicamente (DELETE)
     */
    public boolean eliminarTarea(int idTarea) {
        String sql = "DELETE FROM tareas WHERE id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Archiva una tarea (soft delete)
     */
    public boolean archivarTarea(int idTarea) {
        String sql = "UPDATE tareas SET archivada = TRUE WHERE id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca tareas por título o descripción
     */
    public List<Tarea> buscarTareas(String criterio) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "WHERE (LOWER(t.titulo) LIKE LOWER(?) OR LOWER(t.descripcion) LIKE LOWER(?)) AND t.archivada = FALSE "
                + "ORDER BY t.fecha_limite DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            String searchPattern = "%" + criterio + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Obtiene tareas archivadas de un usuario
     */
    public List<Tarea> getTareasArchivadas(int idUsuario) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT DISTINCT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "LEFT JOIN asignaciones_usuario au ON t.id_tarea = au.id_tarea "
                + "LEFT JOIN asignaciones_equipo ae ON t.id_tarea = ae.id_tarea "
                + "LEFT JOIN equipo_miembros em ON ae.id_equipo = em.id_equipo "
                + "WHERE (au.id_usuario = ? OR em.id_usuario = ?) AND t.archivada = TRUE "
                + "ORDER BY t.fecha_limite DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Obtiene tareas por estado
     */
    public List<Tarea> getTareasPorEstado(int idEstado) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "WHERE t.id_estado = ? AND t.archivada = FALSE "
                + "ORDER BY t.fecha_limite DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Obtiene tareas por prioridad
     */
    public List<Tarea> getTareasPorPrioridad(int idPrioridad) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.*, et.nombre_estado, pt.nombre_prioridad, u.nombre as nombre_creador "
                + "FROM tareas t "
                + "JOIN estados_tarea et ON t.id_estado = et.id_estado "
                + "JOIN prioridades_tarea pt ON t.id_prioridad = pt.id_prioridad "
                + "JOIN usuarios u ON t.creada_por = u.id_usuario "
                + "WHERE t.id_prioridad = ? AND t.archivada = FALSE "
                + "ORDER BY t.fecha_limite DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPrioridad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapResultSetToTarea(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }

    /**
     * Reasigna una tarea a un nuevo usuario o equipo
     */
    public boolean reasignarTarea(int idTarea, Integer nuevoIdUsuario, Integer nuevoIdEquipo) {
        Connection cn = null;
        try {
            cn = con.getConnection();
            cn.setAutoCommit(false);

            // Eliminar asignaciones anteriores
            String deleteUsuario = "DELETE FROM asignaciones_usuario WHERE id_tarea = ?";
            String deleteEquipo = "DELETE FROM asignaciones_equipo WHERE id_tarea = ?";

            try (PreparedStatement ps1 = cn.prepareStatement(deleteUsuario);
                    PreparedStatement ps2 = cn.prepareStatement(deleteEquipo)) {
                ps1.setInt(1, idTarea);
                ps2.setInt(1, idTarea);
                ps1.executeUpdate();
                ps2.executeUpdate();
            }

            // Agregar nueva asignación
            if (nuevoIdUsuario != null && nuevoIdUsuario > 0) {
                String insertUsuario = "INSERT INTO asignaciones_usuario (id_tarea, id_usuario) VALUES (?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(insertUsuario)) {
                    ps.setInt(1, idTarea);
                    ps.setInt(2, nuevoIdUsuario);
                    ps.executeUpdate();
                }
            }

            if (nuevoIdEquipo != null && nuevoIdEquipo > 0) {
                String insertEquipo = "INSERT INTO asignaciones_equipo (id_tarea, id_equipo) VALUES (?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(insertEquipo)) {
                    ps.setInt(1, idTarea);
                    ps.setInt(2, nuevoIdEquipo);
                    ps.executeUpdate();
                }
            }

            cn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (cn != null)
                    cn.rollback();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (cn != null) {
                    cn.setAutoCommit(true);
                    cn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtiene los usuarios asignados a una tarea
     */
    public List<Usuario> getUsuariosAsignadosPorTarea(int idTarea) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.* FROM usuarios u "
                + "JOIN asignaciones_usuario au ON u.id_usuario = au.id_usuario "
                + "WHERE au.id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setFotoUrl(rs.getString("foto_url"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuario.setCodigoEmpleado(rs.getString("codigo_empleado"));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Obtiene los equipos asignados a una tarea
     */
    public List<Equipo> getEquiposAsignadosPorTarea(int idTarea) {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT e.* FROM equipos e "
                + "JOIN asignaciones_equipo ae ON e.id_equipo = ae.id_equipo "
                + "WHERE ae.id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipo equipo = new Equipo();
                    equipo.setIdEquipo(rs.getInt("id_equipo"));
                    equipo.setNombre(rs.getString("nombre"));
                    equipo.setDescripcion(rs.getString("descripcion"));
                    equipo.setIdLider(rs.getInt("id_lider"));
                    equipos.add(equipo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipos;
    }

    private Tarea mapResultSetToTarea(ResultSet rs) throws SQLException {
        Tarea tarea = new Tarea();
        tarea.setIdTarea(rs.getInt("id_tarea"));
        tarea.setTitulo(rs.getString("titulo"));
        tarea.setDescripcion(rs.getString("descripcion"));
        tarea.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        tarea.setFechaLimite(rs.getTimestamp("fecha_limite"));
        tarea.setCreadaPor(rs.getInt("creada_por"));
        tarea.setIdEstado(rs.getInt("id_estado"));
        tarea.setIdPrioridad(rs.getInt("id_prioridad"));

        tarea.setNombreEstado(rs.getString("nombre_estado"));
        tarea.setNombrePrioridad(rs.getString("nombre_prioridad"));

        Usuario creador = new Usuario();
        creador.setIdUsuario(rs.getInt("creada_por"));
        creador.setNombre(rs.getString("nombre_creador"));
        tarea.setCreador(creador);

        return tarea;
    }

}
