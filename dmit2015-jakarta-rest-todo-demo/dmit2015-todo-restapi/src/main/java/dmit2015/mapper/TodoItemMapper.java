package dmit2015.mapper;

import dmit2015.dto.TodoItemDto;
import dmit2015.entity.TodoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * This MapStruct interface contains methods on how to map a Jakarta Persistence entity to a DTO
 * (Data Transfer Object) and a method on how to map a DTO to a JPA entity.
 * <p>
 * The following code snippets shows how to call that class-level methods.
 * {@snippet :
 * //TodoItem newTodoItemEntity = TodoItemMapper.INSTANCE.toEntity(newTodoItemDto);
 * //TodoItemDto newTodoItemDto = TodoItemMapper.INSTANCE.toDto(newTodoItemEntity);
 * }
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoItemMapper {

    TodoItemMapper INSTANCE = Mappers.getMapper( TodoItemMapper.class );

    @Mappings({
            @Mapping(target = "name", source = "task"),
            @Mapping(target = "complete", source = "done"),
    })
    TodoItemDto toDto(TodoItem entity);

    @Mappings({
            @Mapping(target = "task", source = "name"),
            @Mapping(target = "done", source = "complete"),
    })
    TodoItem toEntity(TodoItemDto dto);

}