package org.ilaria.progettosistemidistribuiti.Service.Mapper;

import org.ilaria.progettosistemidistribuiti.Model.DTO.AttachmentDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.NoteDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.NoteViewDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteDTO toDto(Note note);

    @Mapping(source = "ticket.id", target = "id_Ticket")
    @Mapping(source = "ticket.problem_title", target = "ticketTitle")
    NoteViewDTO toDtoView(Note note);

    @Mapping(target = "ticket", ignore = true)
    Note toEntity(NoteDTO noteDTO);

}
