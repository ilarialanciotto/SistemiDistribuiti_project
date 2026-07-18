package org.ilaria.progettosistemidistribuiti.Model.DTO;

import lombok.Data;
import org.ilaria.progettosistemidistribuiti.Model.Level;
import org.ilaria.progettosistemidistribuiti.Model.Problem;

@Data
public class TicketDTO {

    private String problem_title;
    private String description;
    private Problem problem;
    private Level urgency_percepite;

}
