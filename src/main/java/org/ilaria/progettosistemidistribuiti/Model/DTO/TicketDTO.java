package org.ilaria.progettosistemidistribuiti.Model.DTO;

import lombok.Data;
import org.ilaria.progettosistemidistribuiti.Model.Category;
import org.ilaria.progettosistemidistribuiti.Model.Level;
import org.ilaria.progettosistemidistribuiti.Model.State;

import java.sql.Time;

@Data
public class TicketDTO {

    private String problem_title;
    private String description;
    private Category category;
    private Level urgency_percepite;

}
