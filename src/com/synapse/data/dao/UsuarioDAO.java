package com.synapse.data.dao;

import com.synapse.core.models.Usuario;
import com.synapse.data.database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UsuarioDAO {

    private final Conexion con = Conexion.getInstance();

    public Usuario login(String email, String password) {
        System.out.println("=== UsuarioDAO.login() ===");
        System.out.println("Email: " + email);
        System.out.println("Password length: " + password.length());

        String sql = "SELECT u.*, r.nombre_rol, c.id_rol, c.password "
                + "FROM usuarios u "
                + "JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "JOIN roles r ON c.id_rol = r.id_rol "
                + "WHERE u.email = ? AND u.activo = TRUE";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            System.out.println("Ejecutando query...");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ Usuario encontrado en BD");
                    String hashedPassword = rs.getString("password");
                    String hashPreview = hashedPassword.length() > 20
                            ? hashedPassword.substring(0, 20) + "..."
                            : hashedPassword + " (NO HASHEADO - " + hashedPassword.length() + " chars)";
                    System.out.println("Hash en BD: " + hashPreview);

                    // Verificar password con BCrypt
                    System.out.println("Verificando password con BCrypt...");
                    boolean passwordMatch = com.synapse.utils.PasswordHasher.verifyPassword(password, hashedPassword);
                    System.out.println("Password match: " + passwordMatch);

                    if (passwordMatch) {
                        System.out.println("✅ Password correcto, creando usuario");
                        return new Usuario(
                                rs.getInt("id_usuario"),
                                rs.getString("nombre"),
                                rs.getString("email"),
                                rs.getString("foto_url"),
                                rs.getBoolean("activo"),
                                rs.getString("codigo_empleado"),
                                rs.getInt("id_rol"),
                                rs.getString("nombre_rol"));
                    } else {
                        System.out.println("❌ Password incorrecto");
                    }
                } else {
                    System.out.println("❌ Usuario NO encontrado en BD");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error SQL: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=========================");
        return null; // Login fallido
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol, c.id_rol "
                + "FROM usuarios u "
                + "LEFT JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "LEFT JOIN roles r ON c.id_rol = r.id_rol "
                + "ORDER BY u.nombre";

        try (Connection cn = con.getConnection(); // CORREGIDO
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("foto_url"),
                        rs.getBoolean("activo"),
                        rs.getString("codigo_empleado"),
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public List<Usuario> getUsuariosPorRol(String nombreRol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol, c.id_rol "
                + "FROM usuarios u "
                + "JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "JOIN roles r ON c.id_rol = r.id_rol "
                + "WHERE r.nombre_rol = ? AND u.activo = TRUE";

        try (Connection cn = con.getConnection(); // CORREGIDO
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("foto_url"),
                            rs.getBoolean("activo"),
                            rs.getString("codigo_empleado"),
                            rs.getInt("id_rol"),
                            rs.getString("nombre_rol")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public List<Usuario> getMiembrosPorEquipo(int idEquipo) {
        List<Usuario> miembros = new ArrayList<>();
        // Este query es similar al de login(), pero filtrado por equipo_miembros
        String sql = "SELECT u.*, r.nombre_rol, c.id_rol "
                + "FROM usuarios u "
                + "JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "JOIN roles r ON c.id_rol = r.id_rol "
                + "JOIN equipo_miembros em ON u.id_usuario = em.id_usuario "
                + "WHERE em.id_equipo = ? AND u.activo = TRUE";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Reutilizamos el constructor que ya funciona
                    miembros.add(new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("foto_url"),
                            rs.getBoolean("activo"),
                            rs.getString("codigo_empleado"),
                            rs.getInt("id_rol"),
                            rs.getString("nombre_rol")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return miembros;
    }

    /**
     * Crea un nuevo usuario con sus credenciales
     */
    public boolean crearUsuario(Usuario usuario, String password, int idRol) {
        Connection cn = null;
        try {
            cn = con.getConnection();
            cn.setAutoCommit(false);

            // 1. Insertar usuario
            String sqlUsuario = "INSERT INTO usuarios (nombre, email, foto_url, activo, codigo_empleado) "
                    + "VALUES (?, ?, ?, ?, ?)";
            int idUsuarioGenerado;

            try (PreparedStatement ps = cn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getFotoUrl());
                ps.setBoolean(4, usuario.isActivo());
                ps.setString(5, usuario.getCodigoEmpleado());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idUsuarioGenerado = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del usuario creado.");
                    }
                }
            }

            // 2. Insertar credenciales con password hasheado
            String sqlCredenciales = "INSERT INTO credenciales (id_usuario, password, id_rol) VALUES (?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlCredenciales)) {
                ps.setInt(1, idUsuarioGenerado);
                // Hashear la contraseña antes de guardarla
                System.out.println("=== DEBUG crearUsuario ===");
                System.out.println("Password recibido: " + password);
                System.out.println("Password length: " + password.length());
                String hashedPassword = com.synapse.utils.PasswordHasher.hashPassword(password);
                System.out.println("Hash generado length: " + hashedPassword.length());
                System.out
                        .println("Hash preview: " + hashedPassword.substring(0, Math.min(20, hashedPassword.length())));
                ps.setString(2, hashedPassword);
                ps.setInt(3, idRol);
                ps.executeUpdate();
                System.out.println("✅ Credenciales insertadas correctamente");
            }

            cn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (cn != null) {
                    cn.rollback();
                }
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
     * Obtiene un usuario por su ID
     */
    public Usuario getUsuarioPorId(int idUsuario) {
        String sql = "SELECT u.*, r.nombre_rol, c.id_rol "
                + "FROM usuarios u "
                + "LEFT JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "LEFT JOIN roles r ON c.id_rol = r.id_rol "
                + "WHERE u.id_usuario = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("foto_url"),
                            rs.getBoolean("activo"),
                            rs.getString("codigo_empleado"),
                            rs.getInt("id_rol"),
                            rs.getString("nombre_rol"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Elimina un usuario (soft delete - desactiva)
     */
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "UPDATE usuarios SET activo = FALSE WHERE id_usuario = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public boolean cambiarPassword(int idUsuario, String oldPassword, String newPassword) {
        Connection cn = null;
        try {
            cn = con.getConnection();

            // Verificar contraseña actual
            String sqlVerify = "SELECT password FROM credenciales WHERE id_usuario = ?";
            try (PreparedStatement ps = cn.prepareStatement(sqlVerify)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String currentPassword = rs.getString("password");
                        // TODO: Usar hash comparison cuando se implemente BCrypt
                        if (!currentPassword.equals(oldPassword)) {
                            return false; // Contraseña incorrecta
                        }
                    } else {
                        return false; // Usuario no encontrado
                    }
                }
            }

            // Actualizar contraseña
            String sqlUpdate = "UPDATE credenciales SET password = ? WHERE id_usuario = ?";
            try (PreparedStatement ps = cn.prepareStatement(sqlUpdate)) {
                ps.setString(1, newPassword); // TODO: Hash password
                ps.setInt(2, idUsuario);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cambia el rol de un usuario
     */
    public boolean cambiarRol(int idUsuario, int nuevoIdRol) {
        String sql = "UPDATE credenciales SET id_rol = ? WHERE id_usuario = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nuevoIdRol);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida las credenciales de un usuario
     */
    public boolean validarCredenciales(String email, String password) {
        String sql = "SELECT c.password FROM credenciales c "
                + "JOIN usuarios u ON c.id_usuario = u.id_usuario "
                + "WHERE u.email = ? AND u.activo = TRUE";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    // TODO: Usar hash comparison cuando se implemente BCrypt
                    return storedPassword.equals(password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca usuarios por nombre o email
     */
    public List<Usuario> buscarUsuarios(String criterio) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol, c.id_rol "
                + "FROM usuarios u "
                + "LEFT JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "LEFT JOIN roles r ON c.id_rol = r.id_rol "
                + "WHERE (LOWER(u.nombre) LIKE LOWER(?) OR LOWER(u.email) LIKE LOWER(?)) "
                + "AND u.activo = TRUE "
                + "ORDER BY u.nombre";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            String searchPattern = "%" + criterio + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("foto_url"),
                            rs.getBoolean("activo"),
                            rs.getString("codigo_empleado"),
                            rs.getInt("id_rol"),
                            rs.getString("nombre_rol")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Obtiene un usuario por email
     */
    public Usuario getUsuarioPorEmail(String email) {
        String sql = "SELECT u.*, r.nombre_rol, c.id_rol "
                + "FROM usuarios u "
                + "LEFT JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "LEFT JOIN roles r ON c.id_rol = r.id_rol "
                + "WHERE u.email = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("foto_url"),
                            rs.getBoolean("activo"),
                            rs.getString("codigo_empleado"),
                            rs.getInt("id_rol"),
                            rs.getString("nombre_rol"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene un usuario por código de empleado
     */
    public Usuario getUsuarioPorCodigoEmpleado(String codigo) {
        String sql = "SELECT u.*, r.nombre_rol, c.id_rol "
                + "FROM usuarios u "
                + "LEFT JOIN credenciales c ON u.id_usuario = c.id_usuario "
                + "LEFT JOIN roles r ON c.id_rol = r.id_rol "
                + "WHERE u.codigo_empleado = ?";

        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("foto_url"),
                            rs.getBoolean("activo"),
                            rs.getString("codigo_empleado"),
                            rs.getInt("id_rol"),
                            rs.getString("nombre_rol"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Actualizar usuario sin cambiar contraseña
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, email = ? WHERE id_usuario = ?";
        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setInt(3, usuario.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Actualizar usuario con nueva contraseña

    public boolean actualizarUsuarioConPassword(Usuario usuario, String nuevaPassword, String passwordActual) {
        // 1. Verificar contraseña actual
        String sqlGetHash = "SELECT password FROM credenciales WHERE id_usuario = ?";
        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sqlGetHash)) {
            ps.setInt(1, usuario.getIdUsuario());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashActual = rs.getString("password");
                if (!com.synapse.utils.PasswordHasher.verifyPassword(passwordActual, hashActual)) {
                    return false; // Contraseña actual incorrecta
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // 2. Actualizar usuario y contraseña
        String sqlUpdateUser = "UPDATE usuarios SET nombre = ?, email = ? WHERE id_usuario = ?";
        String sqlUpdatePass = "UPDATE credenciales SET password = ? WHERE id_usuario = ?";

        try (Connection cn = con.getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps1 = cn.prepareStatement(sqlUpdateUser);
                    PreparedStatement ps2 = cn.prepareStatement(sqlUpdatePass)) {

                ps1.setString(1, usuario.getNombre());
                ps1.setString(2, usuario.getEmail());
                ps1.setInt(3, usuario.getIdUsuario());
                ps1.executeUpdate();

                String nuevoHash = com.synapse.utils.PasswordHasher.hashPassword(nuevaPassword);
                ps2.setString(1, nuevoHash);
                ps2.setInt(2, usuario.getIdUsuario());
                ps2.executeUpdate();

                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar usuario desde panel de administración (nombre, email, rol)
     */
    public boolean actualizarUsuarioAdmin(Usuario usuario) {
        String sqlUpdateUser = "UPDATE usuarios SET nombre = ?, email = ? WHERE id_usuario = ?";
        String sqlUpdateRol = "UPDATE credenciales SET id_rol = (SELECT id_rol FROM roles WHERE nombre_rol = ?) WHERE id_usuario = ?";

        try (Connection cn = con.getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps1 = cn.prepareStatement(sqlUpdateUser);
                    PreparedStatement ps2 = cn.prepareStatement(sqlUpdateRol)) {

                // Actualizar nombre y email en usuarios
                ps1.setString(1, usuario.getNombre());
                ps1.setString(2, usuario.getEmail());
                ps1.setInt(3, usuario.getIdUsuario());
                ps1.executeUpdate();

                // Actualizar rol en credenciales
                ps2.setString(1, usuario.getRol());
                ps2.setInt(2, usuario.getIdUsuario());
                ps2.executeUpdate();

                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambiar estado activo/inactivo de un usuario
     */
    public boolean cambiarEstadoUsuario(int idUsuario, boolean nuevoEstado) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id_usuario = ?";
        try (Connection cn = con.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
