package org.ilaria.progettosistemidistribuiti.Service.Mapper;


import org.ilaria.progettosistemidistribuiti.Model.DTO.AttachmentDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    AttachmentDTO toDto(Attachment attachment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    Attachment toEntity(AttachmentDTO attachmentDTO);
}
