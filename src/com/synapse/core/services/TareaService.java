package com.synapse.core.services;

import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.core.models.Notificacion;
import com.synapse.data.dao.TareaDAO;
import com.synapse.data.dao.NotificacionDAO;
import com.synapse.data.dao.AdjuntoDAO;
import java.util.List;

/**
 * Servicio Facade para gestión de tareas
 * Patrón: Facade - Simplifica operaciones complejas que involucran múltiples
 * DAOs
 * 
 * @author FERNANDO
 */
public class TareaService {

    private final TareaDAO tareaDAO;
    private final NotificacionDAO notificacionDAO;
    private final AdjuntoDAO adjuntoDAO;

    public TareaService() {
        this.tareaDAO = new TareaDAO();
        this.notificacionDAO = new NotificacionDAO();
        this.adjuntoDAO = new AdjuntoDAO();
    }

    /**
     * Crea una tarea completa con asignación y notificación
     * Facade que orquesta: crear tarea + asignar + notificar
     */
    public boolean crearTareaCompleta(Tarea tarea, Integer idUsuarioAsignado, Integer idEquipoAsignado) {
        try {
            // 1. Crear la tarea
            boolean tareaCreada = tareaDAO.crearTarea(tarea, idUsuarioAsignado, idEquipoAsignado);

            if (!tareaCreada) {
                return false;
            }

            // 2. Crear notificación para el usuario asignado
            if (idUsuarioAsignado != null && idUsuarioAsignado > 0) {
                Notificacion notif = new Notificacion(
                        idUsuarioAsignado,
                        tarea.getIdTarea(),
                        "Se te ha asignado una nueva tarea: " + tarea.getTitulo());
                notificacionDAO.crearNotificacion(notif);
            }

            // 3. Si es asignado a equipo, notificar a todos los miembros
            // TODO: Implementar cuando tengamos EmailService

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifica una tarea con validaciones
     */
    public boolean modificarTarea(Tarea tarea) {
        try {
            // Validaciones
            if (tarea == null || tarea.getIdTarea() <= 0) {
                throw new IllegalArgumentException("Tarea inválida");
            }

            if (tarea.getTitulo() == null || tarea.getTitulo().trim().isEmpty()) {
                throw new IllegalArgumentException("El título es requerido");
            }

            // Actualizar
            return tareaDAO.actualizarTarea(tarea);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Archiva una tarea y notifica
     */
    public boolean archivarTarea(int idTarea, String motivo) {
        try {
            // 1. Archivar la tarea
            boolean archivada = tareaDAO.archivarTarea(idTarea);

            if (!archivada) {
                return false;
            }

            // 2. Obtener usuarios asignados para notificar
            List<Usuario> usuariosAsignados = tareaDAO.getUsuariosAsignadosPorTarea(idTarea);

            // 3. Crear notificaciones
            for (Usuario usuario : usuariosAsignados) {
                Notificacion notif = new Notificacion(
                        usuario.getIdUsuario(),
                        idTarea,
                        "La tarea ha sido archivada. Motivo: " + motivo);
                notificacionDAO.crearNotificacion(notif);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una tarea con validaciones
     */
    public boolean eliminarTarea(int idTarea) {
        try {
            // Validar que la tarea existe
            Tarea tarea = tareaDAO.getTareaPorId(idTarea);
            if (tarea == null) {
                throw new IllegalArgumentException("Tarea no encontrada");
            }

            // Eliminar adjuntos primero
            adjuntoDAO.eliminarAdjuntosPorTarea(idTarea);

            // Eliminar tarea
            return tareaDAO.eliminarTarea(idTarea);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el estado de una tarea y notifica
     */
    public boolean cambiarEstadoTarea(int idTarea, int nuevoEstado) {
        try {
            // 1. Obtener la tarea
            Tarea tarea = tareaDAO.getTareaPorId(idTarea);
            if (tarea == null) {
                return false;
            }

            // 2. Actualizar estado
            boolean actualizado = tareaDAO.actualizarEstadoTarea(idTarea, nuevoEstado);

            if (!actualizado) {
                return false;
            }

            // 3. Notificar a usuarios asignados
            List<Usuario> usuariosAsignados = tareaDAO.getUsuariosAsignadosPorTarea(idTarea);
            String mensajeEstado = obtenerMensajeEstado(nuevoEstado);

            for (Usuario usuario : usuariosAsignados) {
                Notificacion notif = new Notificacion(
                        usuario.getIdUsuario(),
                        idTarea,
                        "La tarea '" + tarea.getTitulo() + "' cambió a estado: " + mensajeEstado);
                notificacionDAO.crearNotificacion(notif);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reasigna una tarea y notifica a los nuevos asignados
     */
    public boolean reasignarTarea(int idTarea, Integer nuevoIdUsuario, Integer nuevoIdEquipo) {
        try {
            // 1. Obtener tarea
            Tarea tarea = tareaDAO.getTareaPorId(idTarea);
            if (tarea == null) {
                return false;
            }

            // 2. Reasignar
            boolean reasignada = tareaDAO.reasignarTarea(idTarea, nuevoIdUsuario, nuevoIdEquipo);

            if (!reasignada) {
                return false;
            }

            // 3. Notificar al nuevo usuario asignado
            if (nuevoIdUsuario != null && nuevoIdUsuario > 0) {
                Notificacion notif = new Notificacion(
                        nuevoIdUsuario,
                        idTarea,
                        "Se te ha reasignado la tarea: " + tarea.getTitulo());
                notificacionDAO.crearNotificacion(notif);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las tareas de un usuario
     */
    public List<Tarea> getTareasPorUsuario(int idUsuario) {
        return tareaDAO.getTareasPorUsuario(idUsuario);
    }

    /**
     * Busca tareas por criterio
     */
    public List<Tarea> buscarTareas(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            throw new IllegalArgumentException("Criterio de búsqueda vacío");
        }
        return tareaDAO.buscarTareas(criterio);
    }

    /**
     * Obtiene tareas archivadas
     */
    public List<Tarea> getTareasArchivadas(int idUsuario) {
        return tareaDAO.getTareasArchivadas(idUsuario);
    }

    /**
     * Obtiene una tarea por ID
     */
    public Tarea getTareaPorId(int idTarea) {
        return tareaDAO.getTareaPorId(idTarea);
    }

    /**
     * Helper: Obtiene mensaje de estado según ID
     */
    private String obtenerMensajeEstado(int idEstado) {
        switch (idEstado) {
            case 1:
                return "Pendiente";
            case 2:
                return "En Progreso";
            case 3:
                return "Completada";
            default:
                return "Estado " + idEstado;
        }
    }

    /**
     * Actualiza una tarea completa (datos + asignación)
     * Combina modificarTarea + reasignarTarea
     */
    public boolean actualizarTareaCompleta(Tarea tarea, Integer idUsuarioAsignado, Integer idEquipoAsignado) {
        try {
            // 1. Validar y actualizar la tarea
            if (!modificarTarea(tarea)) {
                return false;
            }

            // 2. Reasignar si es necesario
            if (idUsuarioAsignado != null || idEquipoAsignado != null) {
                return reasignarTarea(tarea.getIdTarea(), idUsuarioAsignado, idEquipoAsignado);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
