package org.ilaria.progettosistemidistribuiti.Service.Mapper;


import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketDTO toDto(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "attachment", ignore = true)
    @Mapping(target = "start_date" , ignore = true)
    @Mapping(target = "priority_level_AI" , ignore = true)
    @Mapping(target = "category_AI" , ignore = true)
    @Mapping(target = "keyword_AI" , ignore = true)
    @Mapping(target = "notes" , ignore = true)
    Ticket toEntity(TicketDTO ticketDTO);

}
