package com.synapse.core.services.notifications;

import com.synapse.core.models.Tarea;
import java.util.List;
/**
 *
 * @author FERNANDO
 */
public interface TaskObserver {
    void onTaskDue(List<Tarea> tareas);
}
