package dmit2015.mapper;

import dmit2015.dto.StudentDto;
import dmit2015.entity.Student;
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
 * //Student newStudentEntity = StudentMapper.INSTANCE.toEntity(newStudentDto);
 * //StudentDto newStudentDto = StudentMapper.INSTANCE.toDto(newStudentEntity);
 * }
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    StudentMapper INSTANCE = Mappers.getMapper( StudentMapper.class );

    StudentDto toDto(Student entity);

    Student toEntity(StudentDto dto);

}