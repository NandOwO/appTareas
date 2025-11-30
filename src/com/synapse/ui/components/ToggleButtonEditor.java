package com.synapse.ui.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.synapse.ui.components.ToggleListener;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.UsuarioDAO;
import javax.swing.table.DefaultTableModel;

public class ToggleButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private final ToggleButton toggleButton;
    private final JPanel panel;
    private JTable table;
    private int currentRow;

    public ToggleButtonEditor() {
        toggleButton = new ToggleButton();

        // Contenedor para la edición
        panel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        panel.add(toggleButton);
    }

    // Método de la interfaz ToggleListener (debes tener esta interfaz en tu
    // proyecto)
    public interface ToggleListener {
        void onSelected(boolean selected);

        void onAnimated(float animate);
    }

    @Override
    public Object getCellEditorValue() {
        return toggleButton.isSelected();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.currentRow = row;

        // Prepara el editor (el botón) con el valor actual de la celda.
        if (value instanceof Boolean) {
            toggleButton.setSelected((Boolean) value, false);
        }

        // Manejo de color de fondo
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public boolean stopCellEditing() {
        // Guardar el estado en la base de datos
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int modelRow = table.convertRowIndexToModel(currentRow);
            Object usuarioObj = model.getValueAt(modelRow, 0);

            if (usuarioObj instanceof Usuario) {
                Usuario usuario = (Usuario) usuarioObj;
                boolean nuevoEstado = toggleButton.isSelected();

                // Actualizar en BD
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                boolean exito = usuarioDAO.cambiarEstadoUsuario(usuario.getIdUsuario(), nuevoEstado);

                if (exito) {
                    usuario.setActivo(nuevoEstado);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.stopCellEditing();
    }
}
