package com.alexeymelekhov.lms.mapper;

import com.alexeymelekhov.lms.dto.group.GroupCreateDTO;
import com.alexeymelekhov.lms.dto.group.GroupDTO;
import com.alexeymelekhov.lms.dto.group.GroupUpdateDTO;
import com.alexeymelekhov.lms.model.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupDTO toDTO(Group group);

    @Mapping(target = "id", ignore = true)
    Group toEntity(GroupCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateGroupFromDTO(
            GroupUpdateDTO dto,
            @MappingTarget Group group
    );
}
