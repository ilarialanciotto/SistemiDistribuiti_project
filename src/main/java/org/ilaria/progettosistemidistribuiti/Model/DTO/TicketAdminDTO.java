package org.ilaria.progettosistemidistribuiti.Model.DTO;


import lombok.Data;
import org.ilaria.progettosistemidistribuiti.Model.Category;
import org.ilaria.progettosistemidistribuiti.Model.Level;
import org.ilaria.progettosistemidistribuiti.Model.State;
import java.time.LocalDateTime;

@Data
public class TicketAdminDTO {

    private String problem_title;
    private String description;
    private Category category;
    private Level urgency_percepite;
    private Integer priority_level_AI = -1;
    private LocalDateTime start_date;
    private Category category_AI;
    private String keyword_AI;
    private State state = State.sent;

}
