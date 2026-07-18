package org.ilaria.progettosistemidistribuiti.Model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Level {
    low,
    medium,
    high;

    @JsonCreator
    public static Level fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Level.valueOf(value.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
