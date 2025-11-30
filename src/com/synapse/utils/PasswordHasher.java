package com.synapse.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para hash de contraseñas usando BCrypt
 * Proporciona métodos seguros para hashear y verificar contraseñas
 * 
 * @author FERNANDO
 */
public class PasswordHasher {

    // Número de rondas de hashing (10-12 es recomendado)
    private static final int WORK_FACTOR = 12;

    /**
     * Hashea una contraseña en texto plano
     * 
     * @param plainPassword Contraseña en texto plano
     * @return Hash de la contraseña
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verifica si una contraseña coincide con su hash
     * 
     * @param plainPassword  Contraseña en texto plano
     * @param hashedPassword Hash almacenado en la base de datos
     * @return true si la contraseña coincide, false en caso contrario
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Hash inválido
            return false;
        }
    }

    /**
     * Verifica si un hash necesita ser rehashed (si el work factor cambió)
     * 
     * @param hashedPassword Hash a verificar
     * @return true si necesita rehashing
     */
    public static boolean needsRehash(String hashedPassword) {
        if (hashedPassword == null) {
            return true;
        }

        try {
            // Extraer el work factor del hash
            String[] parts = hashedPassword.split("\\$");
            if (parts.length < 4) {
                return true;
            }

            int currentWorkFactor = Integer.parseInt(parts[2]);
            return currentWorkFactor < WORK_FACTOR;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Genera una contraseña temporal aleatoria
     * 
     * @param length Longitud de la contraseña
     * @return Contraseña temporal
     */
    public static String generateTemporaryPassword(int length) {
        if (length < 8) {
            length = 8; // Mínimo 8 caracteres
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}
