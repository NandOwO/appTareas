package com.synapse.core.services.notifications;

import com.synapse.core.models.Notificacion;
import com.synapse.core.models.Tarea;
import com.synapse.data.dao.NotificacionDAO;

/**
 *
 * @author FERNANDO
 */



// import com.synapse.data.dao.UsuarioDAO;

import java.util.List;

/**
 * Patrón Observer (Observador Concreto 1)
 * Escribe una notificación en la base de datos.
 */
public class NotificationService implements TaskObserver {
    
    private final NotificacionDAO notificacionDAO;
    // private final UsuarioDAO usuarioDAO; // O un TareaDAO.getAsignados(idTarea)

    public NotificationService() {
        this.notificacionDAO = new NotificacionDAO();
    }

    @Override
    public void onTaskDue(List<Tarea> tareas) {
        System.out.println("NotificationService: Creando notificaciones en BD...");
        
        for (Tarea tarea : tareas) {
            // Lógica (simplificada): notificar al creador.
            // TODO: Se debe notificar a TODOS los usuarios/miembros de equipo asignados.
            // Esto requeriría un TareaDAO.getUsuariosAsignados(tarea.getIdTarea())
            
            int idUsuarioANotificar = tarea.getCreadaPor(); // Simplificación
            String mensaje = "La tarea '" + tarea.getTitulo() + "' está próxima a vencer.";
            
            Notificacion noti = new Notificacion(idUsuarioANotificar, tarea.getIdTarea(), mensaje);
            notificacionDAO.crearNotificacion(noti);
        }
    }
}