package com.synapse.data.dao;

import com.synapse.core.models.Equipo;
import com.synapse.core.models.Usuario;
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
public class EquipoDAO {

    private final Conexion con = Conexion.getInstance();

    public boolean crearEquipo(Equipo equipo) {
        // El id_lider viene en el objeto 'equipo'
        String sql = "INSERT INTO equipos (nombre, descripcion, id_lider) VALUES (?, ?, ?)";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, equipo.getNombre());
            ps.setString(2, equipo.getDescripcion());
            ps.setInt(3, equipo.getIdLider()); // ¡Clave!

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Equipo> getEquipos() {
        List<Equipo> equipos = new ArrayList<>();
        // Este query usa una subconsulta para contar miembros
        String sql = "SELECT e.*, u.nombre as nombre_lider, "
                + "(SELECT COUNT(*) FROM equipo_miembros em WHERE em.id_equipo = e.id_equipo) as num_miembros "
                + "FROM equipos e "
                + "JOIN usuarios u ON e.id_lider = u.id_usuario "
                + "WHERE e.activo = TRUE";

        try (Connection cn = con.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo equipo = new Equipo();
                equipo.setIdEquipo(rs.getInt("id_equipo"));
                equipo.setNombre(rs.getString("nombre"));
                equipo.setDescripcion(rs.getString("descripcion"));
                equipo.setIdLider(rs.getInt("id_lider"));

                // --- ¡NUEVO! ---
                equipo.setNumeroMiembros(rs.getInt("num_miembros"));
                // ----------------

                Usuario lider = new Usuario();
                lider.setIdUsuario(rs.getInt("id_lider"));
                lider.setNombre(rs.getString("nombre_lider"));
                equipo.setLider(lider);

                equipos.add(equipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipos;
    }

    public List<Usuario> getMiembros(int idEquipo) {
        List<Usuario> miembros = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.email, u.foto_url "
                + "FROM usuarios u "
                + "JOIN equipo_miembros em ON u.id_usuario = em.id_usuario "
                + "WHERE em.id_equipo = ?";

        try (Connection cn = con.getConnection(); // CORREGIDO
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario miembro = new Usuario();
                    miembro.setIdUsuario(rs.getInt("id_usuario"));
                    miembro.setNombre(rs.getString("nombre"));
                    miembro.setEmail(rs.getString("email"));
                    miembro.setFotoUrl(rs.getString("foto_url"));
                    miembros.add(miembro);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return miembros;
    }

    public List<Equipo> getEquiposPorUsuario(int idUsuario) {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT e.*, u.nombre as nombre_lider "
                + "FROM equipos e "
                + "JOIN equipo_miembros em ON e.id_equipo = em.id_equipo "
                + "JOIN usuarios u ON e.id_lider = u.id_usuario "
                + "WHERE em.id_usuario = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipo equipo = new Equipo();
                    equipo.setIdEquipo(rs.getInt("id_equipo"));
                    equipo.setNombre(rs.getString("nombre"));
                    equipo.setDescripcion(rs.getString("descripcion"));
                    equipo.setIdLider(rs.getInt("id_lider"));

                    Usuario lider = new Usuario();
                    lider.setIdUsuario(rs.getInt("id_lider"));
                    lider.setNombre(rs.getString("nombre_lider"));
                    equipo.setLider(lider);

                    equipos.add(equipo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipos;
    }

    public boolean desactivarEquipo(int idEquipo) {
        String sql = "UPDATE equipos SET activo = FALSE WHERE id_equipo = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene un equipo por su ID
     */
    public Equipo getEquipoPorId(int idEquipo) {
        String sql = "SELECT e.*, u.nombre as nombre_lider, "
                + "(SELECT COUNT(*) FROM equipo_miembros em WHERE em.id_equipo = e.id_equipo) as num_miembros "
                + "FROM equipos e "
                + "JOIN usuarios u ON e.id_lider = u.id_usuario "
                + "WHERE e.id_equipo = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Equipo equipo = new Equipo();
                    equipo.setIdEquipo(rs.getInt("id_equipo"));
                    equipo.setNombre(rs.getString("nombre"));
                    equipo.setDescripcion(rs.getString("descripcion"));
                    equipo.setIdLider(rs.getInt("id_lider"));
                    equipo.setNumeroMiembros(rs.getInt("num_miembros"));

                    Usuario lider = new Usuario();
                    lider.setIdUsuario(rs.getInt("id_lider"));
                    lider.setNombre(rs.getString("nombre_lider"));
                    equipo.setLider(lider);

                    return equipo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza un equipo existente
     */
    public boolean actualizarEquipo(Equipo equipo) {
        String sql = "UPDATE equipos SET nombre = ?, descripcion = ?, id_lider = ? WHERE id_equipo = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, equipo.getNombre());
            ps.setString(2, equipo.getDescripcion());
            ps.setInt(3, equipo.getIdLider());
            ps.setInt(4, equipo.getIdEquipo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un equipo físicamente (DELETE)
     */
    public boolean eliminarEquipo(int idEquipo) {
        String sql = "DELETE FROM equipos WHERE id_equipo = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Agrega un miembro a un equipo
     */
    public boolean agregarMiembro(int idEquipo, int idUsuario) {
        String sql = "INSERT INTO equipo_miembros (id_equipo, id_usuario) VALUES (?, ?)";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remueve un miembro de un equipo
     */
    public boolean removerMiembro(int idEquipo, int idUsuario) {
        String sql = "DELETE FROM equipo_miembros WHERE id_equipo = ? AND id_usuario = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el líder de un equipo
     */
    public boolean cambiarLider(int idEquipo, int nuevoIdLider) {
        String sql = "UPDATE equipos SET id_lider = ? WHERE id_equipo = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nuevoIdLider);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un usuario es miembro de un equipo
     */
    public boolean esMiembro(int idEquipo, int idUsuario) {
        String sql = "SELECT COUNT(*) as count FROM equipo_miembros WHERE id_equipo = ? AND id_usuario = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca equipos por nombre o descripción
     */
    public List<Equipo> buscarEquipos(String criterio) {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT e.*, u.nombre as nombre_lider, "
                + "(SELECT COUNT(*) FROM equipo_miembros em WHERE em.id_equipo = e.id_equipo) as num_miembros "
                + "FROM equipos e "
                + "JOIN usuarios u ON e.id_lider = u.id_usuario "
                + "WHERE (LOWER(e.nombre) LIKE LOWER(?) OR LOWER(e.descripcion) LIKE LOWER(?)) "
                + "AND e.activo = TRUE "
                + "ORDER BY e.nombre";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            String searchPattern = "%" + criterio + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipo equipo = new Equipo();
                    equipo.setIdEquipo(rs.getInt("id_equipo"));
                    equipo.setNombre(rs.getString("nombre"));
                    equipo.setDescripcion(rs.getString("descripcion"));
                    equipo.setIdLider(rs.getInt("id_lider"));
                    equipo.setNumeroMiembros(rs.getInt("num_miembros"));

                    Usuario lider = new Usuario();
                    lider.setIdUsuario(rs.getInt("id_lider"));
                    lider.setNombre(rs.getString("nombre_lider"));
                    equipo.setLider(lider);

                    equipos.add(equipo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipos;
    }

    /**
     * Cuenta la cantidad de miembros de un equipo
     */
    public int contarMiembros(int idEquipo) {
        String sql = "SELECT COUNT(*) FROM equipo_miembros WHERE id_equipo = ?";
        try (Connection cn = con.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Obtiene los equipos donde el usuario es líder
     */
    public List<Equipo> getEquiposPorLider(int idLider) {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT e.id_equipo, e.nombre, e.descripcion, e.id_lider " +
                "FROM equipos e " +
                "WHERE e.id_lider = ? AND e.activo = TRUE";

        try (Connection cn = con.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idLider);
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
}
