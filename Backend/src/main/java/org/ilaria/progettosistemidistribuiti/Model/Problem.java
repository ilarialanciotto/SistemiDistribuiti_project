package org.ilaria.progettosistemidistribuiti.Model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Problem {
     software,
     hardware,
     web_service,
     network_configuration,
     generic;

     @JsonCreator
     public static Problem fromString(String value) {
          if (value == null) {
               return null;
          }
          try {
               return Problem.valueOf(value.toLowerCase());
          } catch (IllegalArgumentException e) {
               return null;
          }
     }
}
