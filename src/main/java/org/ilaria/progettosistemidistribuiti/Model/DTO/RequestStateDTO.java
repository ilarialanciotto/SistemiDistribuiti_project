package org.ilaria.progettosistemidistribuiti.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestStateDTO {

    private TicketAdminDTO ticketAdmin;
    private String newState;
}
