package com.synapse.core.services.reports;
import com.synapse.core.models.Tarea;
import java.util.List;
/**
 *
 * @author FERNANDO
 */
public interface IReporteStrategy {
    
    /**
     * Genera un reporte a partir de una lista de tareas.
     * La implementación decidirá el formato (PDF, Excel, etc.) y
     * cómo obtener la ruta de guardado (ej. JFileChooser).
     * * @param tareas Lista de tareas a exportar.
     * @return true si la exportación fue exitosa, false en caso contrario.
     */
    boolean generar(List<Tarea> tareas);
}