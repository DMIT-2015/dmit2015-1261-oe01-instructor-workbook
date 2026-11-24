package dmit2015.mapper;

import dmit2015.dto.TodoItemDto;
import dmit2015.entity.TodoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoItemMapper {

    TodoItemMapper INSTANCE = Mappers.getMapper(TodoItemMapper.class);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "name", source = "task"),
        @Mapping(target = "complete", source = "done"),
        @Mapping(target = "version", source = "version"),
    })
    TodoItemDto toDto(TodoItem entity);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "task", source = "name"),
        @Mapping(target = "done", source = "complete"),
        @Mapping(target = "version", source = "version"),
    })
    TodoItem toEntity(TodoItemDto dto);

}