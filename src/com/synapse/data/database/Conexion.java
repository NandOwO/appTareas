package com.synapse.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static Conexion instancia;

    private final String USUARIO = "postgres";
    private final String CONTRASENA = "postgres";
    private final String BD = "synapse_db";
    private final String IP = "localhost";
    private final String PUERTO = "5433";

    private final String CADENA = "jdbc:postgresql://" + IP + ":" + PUERTO + "/" + BD;

    private Conexion() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de PostgreSQL no encontrado.");
            e.printStackTrace();
        }
    }

    public static Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CADENA, USUARIO, CONTRASENA);
    }
}