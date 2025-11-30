package com.synapse.data.dao;

import com.synapse.core.models.Notificacion;
import com.synapse.data.database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FERNANDO
 */
public class NotificacionDAO {

    private final Conexion con = Conexion.getInstance();

    public boolean crearNotificacion(Notificacion noti) {
        String sql = "INSERT INTO notificaciones (id_usuario, id_tarea, mensaje) VALUES (?, ?, ?)";
        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, noti.getIdUsuario());
            ps.setInt(2, noti.getIdTarea());
            ps.setString(3, noti.getMensaje());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las notificaciones de un usuario
     */
    public List<Notificacion> getNotificacionesPorUsuario(int idUsuario) {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones WHERE id_usuario = ? ORDER BY fecha DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificaciones;
    }

    /**
     * Obtiene las notificaciones no leídas de un usuario
     */
    public List<Notificacion> getNotificacionesNoLeidas(int idUsuario) {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones WHERE id_usuario = ? AND leida = FALSE ORDER BY fecha DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificaciones;
    }

    /**
     * Obtiene el número de notificaciones no leídas
     */
    public int getNumeroNotificacionesNoLeidas(int idUsuario) {
        String sql = "SELECT COUNT(*) as count FROM notificaciones WHERE id_usuario = ? AND leida = FALSE";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Marca una notificación como leída
     */
    public boolean marcarComoLeida(int idNotificacion) {
        String sql = "UPDATE notificaciones SET leida = TRUE WHERE id_notificacion = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idNotificacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     */
    public boolean marcarTodasComoLeidas(int idUsuario) {
        String sql = "UPDATE notificaciones SET leida = TRUE WHERE id_usuario = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una notificación
     */
    public boolean eliminarNotificacion(int idNotificacion) {
        String sql = "DELETE FROM notificaciones WHERE id_notificacion = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idNotificacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mapea un ResultSet a un objeto Notificacion
     */
    private Notificacion mapResultSetToNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdNotificacion(rs.getInt("id_notificacion"));
        notificacion.setIdUsuario(rs.getInt("id_usuario"));
        notificacion.setIdTarea(rs.getInt("id_tarea"));
        notificacion.setMensaje(rs.getString("mensaje"));
        notificacion.setFecha(rs.getTimestamp("fecha"));
        notificacion.setLeida(rs.getBoolean("leida"));
        return notificacion;
    }
}
