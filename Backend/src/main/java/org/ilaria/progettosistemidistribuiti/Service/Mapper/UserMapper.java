package org.ilaria.progettosistemidistribuiti.Service.Mapper;

import org.ilaria.progettosistemidistribuiti.Model.DTO.UserDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO userDTO);
}

