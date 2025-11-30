package com.synapse.ui.components;

import com.synapse.core.models.Tarea;
import com.synapse.core.services.TareaService;
import com.synapse.ui.views.empleado.VerTareaDialog;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import raven.toast.Notifications;

public class TaskActionsEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private final TaskActionsPanel panel;
    private int currentRow;
    private JTable table;
    private final TareaService tareaService;

    public TaskActionsEditor() {
        this.panel = new TaskActionsPanel();
        this.tareaService = new TareaService();

        // Añadir listeners a los 3 botones
        this.panel.btnVer.addActionListener(this);
        this.panel.btnEditar.addActionListener(this);
        this.panel.btnEliminar.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        this.table = table;
        this.currentRow = row;

        panel.setOpaque(true);
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Acciones";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Obtener la tarea de la fila
        int modelRow = table.convertRowIndexToModel(currentRow);
        Tarea tarea = (Tarea) table.getModel().getValueAt(modelRow, 0);

        if (e.getSource() == panel.btnVer) {
            // *** ACCIÓN: Ver Tarea ***
            verTarea(tarea);

        } else if (e.getSource() == panel.btnEditar) {
            // *** ACCIÓN: Editar Tarea Completa ***
            editarTarea(tarea, modelRow);

        } else if (e.getSource() == panel.btnEliminar) {
            // *** ACCIÓN: Archivar Tarea ***
            archivarTarea(tarea, modelRow);
        }

        // Detener la edición de la celda
        fireEditingStopped();
    }

    /**
     * Abre el dialog para ver los detalles de la tarea
     */
    private void verTarea(Tarea tarea) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(table);
        VerTareaDialog dialog = new VerTareaDialog(parentFrame, true, tarea);
        dialog.setVisible(true);
    }

    /**
     * Abre el dialog para editar la tarea completa
     */
    private void editarTarea(Tarea tarea, int modelRow) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(table);
        com.synapse.ui.views.gerente.EditarTareaDialog dialog = new com.synapse.ui.views.gerente.EditarTareaDialog(
                parentFrame, true, tarea);
        dialog.setVisible(true);

        // Si se guardaron cambios, actualizar la tabla
        if (dialog.isExitoso()) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            // Actualizar las columnas visibles
            model.setValueAt(tarea, modelRow, 0); // Columna 0 = Tarea (objeto completo)
            model.setValueAt(tarea.getNombrePrioridad(), modelRow, 1); // Columna 1 = Prioridad
            model.setValueAt(tarea.getNombreEstado(), modelRow, 2); // Columna 2 = Estado
            model.setValueAt(tarea.getFechaLimite(), modelRow, 4); // Columna 4 = Fecha Límite
            model.fireTableRowsUpdated(modelRow, modelRow);

            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.TOP_CENTER,
                    "✅ Tarea actualizada correctamente");
        }
    }

    /**
     * Archiva la tarea (cambia estado a Archivada)
     */
    private void archivarTarea(Tarea tarea, int modelRow) {
        int confirmacion = JOptionPane.showConfirmDialog(
                table,
                "¿Estás seguro de archivar la tarea:\n\"" + tarea.getTitulo() + "\"?",
                "Confirmar Archivado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String motivo = JOptionPane.showInputDialog(
                    table,
                    "Motivo del archivado (opcional):",
                    "Archivar Tarea",
                    JOptionPane.QUESTION_MESSAGE);

            if (motivo != null) { // Usuario no canceló
                boolean exito = tareaService.archivarTarea(tarea.getIdTarea(), motivo);

                if (exito) {
                    // Eliminar la fila de la tabla
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.removeRow(modelRow);

                    Notifications.getInstance().show(
                            Notifications.Type.SUCCESS,
                            Notifications.Location.TOP_CENTER,
                            "✅ Tarea archivada exitosamente");
                } else {
                    Notifications.getInstance().show(
                            Notifications.Type.ERROR,
                            Notifications.Location.TOP_CENTER,
                            "❌ Error al archivar la tarea");
                }
            }
        }
    }
}