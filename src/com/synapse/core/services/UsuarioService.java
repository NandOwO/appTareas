package com.synapse.core.services;

import com.synapse.core.models.Usuario;
import com.synapse.data.dao.UsuarioDAO;
import java.util.List;

/**
 * Servicio Facade para gestión de usuarios
 * Patrón: Facade - Simplifica operaciones de usuarios con validaciones
 * 
 * @author FERNANDO
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Crea un usuario completo con credenciales
     */
    public boolean crearUsuarioCompleto(Usuario usuario, String password, int idRol) {
        try {
            // Validaciones
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no puede ser nulo");
            }

            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es requerido");
            }

            if (usuario.getEmail() == null || !validarEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("Email inválido");
            }

            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }

            // Verificar que el email no exista
            Usuario existente = usuarioDAO.getUsuarioPorEmail(usuario.getEmail());
            if (existente != null) {
                throw new IllegalArgumentException("El email ya está registrado");
            }

            // Crear usuario
            // TODO: Hash password cuando se implemente PasswordHasher
            return usuarioDAO.crearUsuario(usuario, password, idRol);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza el perfil de un usuario
     */
    public boolean actualizarPerfil(Usuario usuario) {
        try {
            // Validaciones
            if (usuario == null || usuario.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("Usuario inválido");
            }

            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es requerido");
            }

            if (usuario.getEmail() == null || !validarEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("Email inválido");
            }

            // Verificar que el email no esté en uso por otro usuario
            Usuario existente = usuarioDAO.getUsuarioPorEmail(usuario.getEmail());
            if (existente != null && existente.getIdUsuario() != usuario.getIdUsuario()) {
                throw new IllegalArgumentException("El email ya está en uso");
            }

            return usuarioDAO.actualizarUsuario(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia la contraseña de un usuario de forma segura
     */
    public boolean cambiarPasswordSeguro(int idUsuario, String oldPassword, String newPassword) {
        try {
            // Validaciones
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Contraseña actual requerida");
            }

            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
            }

            if (oldPassword.equals(newPassword)) {
                throw new IllegalArgumentException("La nueva contraseña debe ser diferente");
            }

            // Cambiar contraseña
            // TODO: Hash passwords cuando se implemente PasswordHasher
            return usuarioDAO.cambiarPassword(idUsuario, oldPassword, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Desactiva un usuario (soft delete)
     */
    public boolean desactivarUsuario(int idUsuario) {
        try {
            // Verificar que el usuario existe
            Usuario usuario = usuarioDAO.getUsuarioPorId(idUsuario);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            return usuarioDAO.eliminarUsuario(idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el rol de un usuario
     */
    public boolean cambiarRol(int idUsuario, int nuevoIdRol) {
        try {
            // Validar que el usuario existe
            Usuario usuario = usuarioDAO.getUsuarioPorId(idUsuario);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            // Validar rol (1=admin, 2=gerente, 3=empleado)
            if (nuevoIdRol < 1 || nuevoIdRol > 3) {
                throw new IllegalArgumentException("Rol inválido");
            }

            return usuarioDAO.cambiarRol(idUsuario, nuevoIdRol);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida credenciales de login
     */
    public Usuario login(String email, String password) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email requerido");
            }

            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Contraseña requerida");
            }

            // TODO: Hash password cuando se implemente PasswordHasher
            return usuarioDAO.login(email, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca usuarios por criterio
     */
    public List<Usuario> buscarUsuarios(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            throw new IllegalArgumentException("Criterio de búsqueda vacío");
        }
        return usuarioDAO.buscarUsuarios(criterio);
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> getUsuarios() {
        return usuarioDAO.getUsuarios();
    }

    /**
     * Obtiene un usuario por ID
     */
    public Usuario getUsuarioPorId(int idUsuario) {
        return usuarioDAO.getUsuarioPorId(idUsuario);
    }

    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> getUsuariosPorRol(String nombreRol) {
        return usuarioDAO.getUsuariosPorRol(nombreRol);
    }

    /**
     * Valida formato de email
     */
    private boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Validación simple de email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
