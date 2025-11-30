/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.synapse.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author FERNANDO
 */
public class PasswordBuilder {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_+=<>?";

    private int length = 12;
    private boolean useLowercase = true;
    private boolean useUppercase = true;
    private boolean useDigits = true;
    private boolean useSymbols = true;

    private final SecureRandom random = new SecureRandom();

    public PasswordBuilder setLength(int length) {
        this.length = length;
        return this;
    }

    public PasswordBuilder useLowercase(boolean useLowercase) {
        this.useLowercase = useLowercase;
        return this;
    }

    public PasswordBuilder useUppercase(boolean useUppercase) {
        this.useUppercase = useUppercase;
        return this;
    }

    public PasswordBuilder useDigits(boolean useDigits) {
        this.useDigits = useDigits;
        return this;
    }

    public PasswordBuilder useSymbols(boolean useSymbols) {
        this.useSymbols = useSymbols;
        return this;
    }

    public Password build() {
        if (!useLowercase && !useUppercase && !useDigits && !useSymbols) {
            throw new IllegalArgumentException("Debe seleccionar al menos un tipo de caracter.");
        }

        StringBuilder password = new StringBuilder(length);
        String validChars = "";

        if (useLowercase) {
            validChars += LOWERCASE;
        }
        if (useUppercase) {
            validChars += UPPERCASE;
        }
        if (useDigits) {
            validChars += DIGITS;
        }
        if (useSymbols) {
            validChars += SYMBOLS;
        }

        List<Character> mandatoryChars = new ArrayList<>();

        if (useLowercase) {
            mandatoryChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        if (useUppercase) {
            mandatoryChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (useDigits) {
            mandatoryChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        if (useSymbols) {
            mandatoryChars.add(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }

        for (int i = mandatoryChars.size(); i < length; i++) {
            mandatoryChars.add(validChars.charAt(random.nextInt(validChars.length())));
        }

        Collections.shuffle(mandatoryChars, random);

        for (Character c : mandatoryChars) {
            password.append(c);
        }

        return new Password(password.toString());
    }

}
