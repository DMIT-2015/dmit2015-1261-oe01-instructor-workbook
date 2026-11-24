package dmit2015.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemDto {

    private Long id;

    private String name;

    private boolean complete;

    private Integer version;

    public static TodoItemDto of(Faker faker) {
        TodoItemDto dto = new TodoItemDto();
        dto.setName("Play " + faker.videoGame().title() + " on " + faker.videoGame().genre());
        dto.setComplete(faker.bool().bool());
        return dto;
    }


}