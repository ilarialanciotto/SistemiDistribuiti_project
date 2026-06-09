package org.ilaria.progettosistemidistribuiti.Service.Mapper;

import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketAdminDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketAdminMapper {

    TicketAdminDTO toDto(Ticket ticket);

    @Mapping(target = "attachment", ignore = true)
    Ticket toEntity(TicketAdminDTO ticketAdminDTO);

}