package org.ilaria.progettosistemidistribuiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProgettoSistemiDistribuitiApplication {


    public static void main(String[] args) {
        SpringApplication.run(ProgettoSistemiDistribuitiApplication.class, args);
    }

}
