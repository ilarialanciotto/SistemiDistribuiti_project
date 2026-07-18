package org.ilaria.progettosistemidistribuiti.Service.Mapper;

import org.ilaria.progettosistemidistribuiti.Model.DTO.AttachmentDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketAdminDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Attachment;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketAdminMapper {

    TicketAdminDTO toDto(Ticket ticket);

    Ticket toEntity(TicketAdminDTO ticketAdminDTO);


}