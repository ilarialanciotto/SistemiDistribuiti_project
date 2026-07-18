package org.ilaria.progettosistemidistribuiti.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TicketReportDTO {

    private long totalTickets;
    private int closedRate;
    private long criticalUnresolved;
    private List<CategoryReportDTO> highPriorityDistribution;

}
