package com.synapse.core.services;

import com.synapse.core.models.Equipo;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.data.dao.UsuarioDAO;
import java.util.List;

/**
 * Servicio Facade para gestión de equipos
 * Patrón: Facade - Simplifica operaciones de equipos y miembros
 * 
 * @author FERNANDO
 */
public class EquipoService {

    private final EquipoDAO equipoDAO;
    private final UsuarioDAO usuarioDAO;

    public EquipoService() {
        this.equipoDAO = new EquipoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Crea un equipo completo con miembros iniciales
     */
    public boolean crearEquipoCompleto(Equipo equipo, List<Integer> idsMiembros) {
        try {
            // Validaciones
            if (equipo == null) {
                throw new IllegalArgumentException("Equipo no puede ser nulo");
            }

            if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del equipo es requerido");
            }

            if (equipo.getIdLider() <= 0) {
                throw new IllegalArgumentException("El equipo debe tener un líder");
            }

            // Verificar que el líder existe
            Usuario lider = usuarioDAO.getUsuarioPorId(equipo.getIdLider());
            if (lider == null) {
                throw new IllegalArgumentException("El líder no existe");
            }

            // 1. Crear el equipo
            boolean equipoCreado = equipoDAO.crearEquipo(equipo);
            if (!equipoCreado) {
                return false;
            }

            // 2. Agregar miembros si se proporcionaron
            if (idsMiembros != null && !idsMiembros.isEmpty()) {
                return agregarMiembrosAlEquipo(equipo.getIdEquipo(), idsMiembros);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Agrega múltiples miembros a un equipo
     */
    public boolean agregarMiembrosAlEquipo(int idEquipo, List<Integer> idsUsuarios) {
        try {
            // Validar que el equipo existe
            Equipo equipo = equipoDAO.getEquipoPorId(idEquipo);
            if (equipo == null) {
                throw new IllegalArgumentException("Equipo no encontrado");
            }

            // Agregar cada miembro
            for (Integer idUsuario : idsUsuarios) {
                // Verificar que el usuario existe
                Usuario usuario = usuarioDAO.getUsuarioPorId(idUsuario);
                if (usuario == null) {
                    System.err.println("Usuario " + idUsuario + " no encontrado, omitiendo...");
                    continue;
                }

                // Verificar que no sea ya miembro
                if (equipoDAO.esMiembro(idEquipo, idUsuario)) {
                    System.out.println("Usuario " + idUsuario + " ya es miembro del equipo");
                    continue;
                }

                // Agregar miembro
                equipoDAO.agregarMiembro(idEquipo, idUsuario);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remueve un miembro del equipo con validaciones
     */
    public boolean removerMiembroDelEquipo(int idEquipo, int idUsuario) {
        try {
            // Validar que el equipo existe
            Equipo equipo = equipoDAO.getEquipoPorId(idEquipo);
            if (equipo == null) {
                throw new IllegalArgumentException("Equipo no encontrado");
            }

            // No permitir remover al líder
            if (equipo.getIdLider() == idUsuario) {
                throw new IllegalArgumentException("No se puede remover al líder del equipo");
            }

            // Verificar que es miembro
            if (!equipoDAO.esMiembro(idEquipo, idUsuario)) {
                throw new IllegalArgumentException("El usuario no es miembro del equipo");
            }

            return equipoDAO.removerMiembro(idEquipo, idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el líder de un equipo
     */
    public boolean cambiarLider(int idEquipo, int nuevoIdLider) {
        try {
            // Validar que el equipo existe
            Equipo equipo = equipoDAO.getEquipoPorId(idEquipo);
            if (equipo == null) {
                throw new IllegalArgumentException("Equipo no encontrado");
            }

            // Validar que el nuevo líder existe
            Usuario nuevoLider = usuarioDAO.getUsuarioPorId(nuevoIdLider);
            if (nuevoLider == null) {
                throw new IllegalArgumentException("El nuevo líder no existe");
            }

            // Verificar que el nuevo líder es miembro del equipo
            if (!equipoDAO.esMiembro(idEquipo, nuevoIdLider)) {
                // Si no es miembro, agregarlo primero
                equipoDAO.agregarMiembro(idEquipo, nuevoIdLider);
            }

            return equipoDAO.cambiarLider(idEquipo, nuevoIdLider);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un equipo
     */
    public boolean actualizarEquipo(Equipo equipo) {
        try {
            // Validaciones
            if (equipo == null || equipo.getIdEquipo() <= 0) {
                throw new IllegalArgumentException("Equipo inválido");
            }

            if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del equipo es requerido");
            }

            return equipoDAO.actualizarEquipo(equipo);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un equipo
     */
    public boolean eliminarEquipo(int idEquipo) {
        try {
            // Verificar que el equipo existe
            Equipo equipo = equipoDAO.getEquipoPorId(idEquipo);
            if (equipo == null) {
                throw new IllegalArgumentException("Equipo no encontrado");
            }

            // TODO: Verificar que no tenga tareas asignadas antes de eliminar

            return equipoDAO.eliminarEquipo(idEquipo);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Desactiva un equipo (soft delete)
     */
    public boolean desactivarEquipo(int idEquipo) {
        try {
            // Verificar que el equipo existe
            Equipo equipo = equipoDAO.getEquipoPorId(idEquipo);
            if (equipo == null) {
                throw new IllegalArgumentException("Equipo no encontrado");
            }

            return equipoDAO.desactivarEquipo(idEquipo);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todos los equipos
     */
    public List<Equipo> getEquipos() {
        return equipoDAO.getEquipos();
    }

    /**
     * Obtiene un equipo por ID
     */
    public Equipo getEquipoPorId(int idEquipo) {
        return equipoDAO.getEquipoPorId(idEquipo);
    }

    /**
     * Busca equipos por criterio
     */
    public List<Equipo> buscarEquipos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            throw new IllegalArgumentException("Criterio de búsqueda vacío");
        }
        return equipoDAO.buscarEquipos(criterio);
    }

    /**
     * Obtiene los miembros de un equipo
     */
    public List<Usuario> getMiembros(int idEquipo) {
        return equipoDAO.getMiembros(idEquipo);
    }

    /**
     * Obtiene los equipos de un usuario
     */
    public List<Equipo> getEquiposPorUsuario(int idUsuario) {
        return equipoDAO.getEquiposPorUsuario(idUsuario);
    }

    /**
     * Verifica si un usuario es miembro de un equipo
     */
    public boolean esMiembro(int idEquipo, int idUsuario) {
        return equipoDAO.esMiembro(idEquipo, idUsuario);
    }
}
