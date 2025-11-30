package com.synapse.core.services;

import com.synapse.core.models.Usuario;
import com.synapse.ui.views.admin.DashbordAdmin;
import com.synapse.ui.views.empleado.DashbordEmpleado;
import com.synapse.ui.views.gerente.DashbordGerente;
import javax.swing.JPanel;

/**
 * Patrón Factory Method
 * Encapsula la lógica de instanciación de los Dashboards.
 * El MainForm no necesita saber qué tipo de dashboard crear,
 * solo le pide a la fábrica que lo haga.
 * 
 * @author FERNANDO
 */
public class ViewFactory {

    /**
     * Crea y devuelve el panel de dashboard apropiado para el rol del usuario.
     * 
     * @param usuario El usuario que ha iniciado sesión.
     * @return El JPanel del dashboard correspondiente.
     */
    public static JPanel crearDashboard(Usuario usuario) {

        // El DAO ya nos dio el 'nombre_rol' en el objeto Usuario
        String rol = usuario.getRol();

        if (rol == null) {
            System.err.println("El usuario no tiene un rol asignado. Cargando vista por defecto.");
            return new DashbordEmpleado(usuario); // Principio de menor privilegio
        }

        switch (rol) {
            case "administrador":
                return new DashbordAdmin(usuario);
            case "gerente":
                return new DashbordGerente(usuario);
            case "empleado":
                return new DashbordEmpleado(usuario);
            default:
                System.err.println("Rol desconocido: " + rol + ". Cargando vista por defecto.");
                return new DashbordEmpleado(usuario);
        }
    }
}