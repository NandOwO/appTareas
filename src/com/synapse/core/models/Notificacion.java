package com.synapse.core.models;

import java.sql.Timestamp;

/**
 *
 * @author FERNANDO
 */

public class Notificacion {
    private int idNotificacion;
    private int idUsuario;
    private int idTarea;
    private String mensaje;
    private Timestamp fecha;
    private boolean leida;

    // Constructor vacío para el DAO
    public Notificacion() {
    }

    // Constructor para crear
    public Notificacion(int idUsuario, int idTarea, String mensaje) {
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
        this.mensaje = mensaje;
    }

    /**
     * @return the idNotificacion
     */
    public int getIdNotificacion() {
        return idNotificacion;
    }

    /**
     * @param idNotificacion the idNotificacion to set
     */
    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    /**
     * @return the idUsuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @return the idTarea
     */
    public int getIdTarea() {
        return idTarea;
    }

    /**
     * @param idTarea the idTarea to set
     */
    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the fecha
     */
    public Timestamp getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the leida
     */
    public boolean isLeida() {
        return leida;
    }

    /**
     * @param leida the leida to set
     */
    public void setLeida(boolean leida) {
        this.leida = leida;
    }

}
