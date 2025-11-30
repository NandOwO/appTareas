package com.synapse.data.dao;

import com.synapse.core.models.Adjunto;
import com.synapse.data.database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestión de adjuntos de tareas
 * Los archivos físicos se envían por email, aquí solo se almacenan metadatos
 * 
 * @author FERNANDO
 */
public class AdjuntoDAO {

    private final Conexion con = Conexion.getInstance();

    /**
     * Agrega un adjunto y retorna el ID generado
     */
    public int agregarAdjunto(Adjunto adjunto) {
        String sql = "INSERT INTO adjuntos (id_tarea, nombre_archivo, ruta_archivo, tipo_archivo, tamanio_bytes) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = con.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, adjunto.getIdTarea());
            ps.setString(2, adjunto.getNombreArchivo());
            ps.setString(3, adjunto.getRutaArchivo());
            ps.setString(4, adjunto.getTipoArchivo());
            ps.setLong(5, adjunto.getTamanioBytes());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Obtiene todos los adjuntos de una tarea
     */
    public List<Adjunto> getAdjuntosPorTarea(int idTarea) {
        List<Adjunto> adjuntos = new ArrayList<>();
        String sql = "SELECT * FROM adjuntos WHERE id_tarea = ? ORDER BY fecha_subida DESC";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    adjuntos.add(mapResultSetToAdjunto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adjuntos;
    }

    /**
     * Obtiene un adjunto por su ID
     */
    public Adjunto getAdjuntoPorId(int idAdjunto) {
        String sql = "SELECT * FROM adjuntos WHERE id_adjunto = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAdjunto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdjunto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Elimina un adjunto
     */
    public boolean eliminarAdjunto(int idAdjunto) {
        String sql = "DELETE FROM adjuntos WHERE id_adjunto = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAdjunto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina todos los adjuntos de una tarea
     */
    public boolean eliminarAdjuntosPorTarea(int idTarea) {
        String sql = "DELETE FROM adjuntos WHERE id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene el tamaño total de adjuntos de una tarea
     */
    public long getTamañoTotalAdjuntos(int idTarea) {
        String sql = "SELECT COALESCE(SUM(tamanio_bytes), 0) as total FROM adjuntos WHERE id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Obtiene el número de adjuntos de una tarea
     */
    public int getNumeroAdjuntos(int idTarea) {
        String sql = "SELECT COUNT(*) as count FROM adjuntos WHERE id_tarea = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
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
     * Mapea un ResultSet a un objeto Adjunto
     */
    private Adjunto mapResultSetToAdjunto(ResultSet rs) throws SQLException {
        Adjunto adjunto = new Adjunto();
        adjunto.setIdAdjunto(rs.getInt("id_adjunto"));
        adjunto.setIdTarea(rs.getInt("id_tarea"));
        adjunto.setNombreArchivo(rs.getString("nombre_archivo"));
        adjunto.setRutaArchivo(rs.getString("ruta_archivo"));
        adjunto.setTipoArchivo(rs.getString("tipo_archivo"));
        adjunto.setTamanioBytes(rs.getLong("tamanio_bytes"));
        adjunto.setFechaSubida(rs.getTimestamp("fecha_subida"));
        return adjunto;
    }
}
