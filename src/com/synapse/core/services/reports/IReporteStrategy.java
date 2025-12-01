package com.synapse.core.services.reports;
import com.synapse.core.models.Tarea;
import java.util.List;
/**
 *
 * @author FERNANDO
 */
public interface IReporteStrategy {
    
    boolean generar(List<Tarea> tareas);
}