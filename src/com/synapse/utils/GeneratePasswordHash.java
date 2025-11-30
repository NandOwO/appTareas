package com.synapse.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Generador de hash para contraseñas
 * Uso: ejecutar este archivo para generar hashes BCrypt
 */
public class GeneratePasswordHash {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));

        System.out.println("Password: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("\nVerificación: " + BCrypt.checkpw(password, hash));
    }
}
