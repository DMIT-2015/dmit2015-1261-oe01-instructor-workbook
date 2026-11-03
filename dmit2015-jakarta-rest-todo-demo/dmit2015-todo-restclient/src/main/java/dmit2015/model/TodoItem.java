package dmit2015.model;

import lombok.Data;
import net.datafaker.Faker;

@Data
public class TodoItem {

    private Long id;
    private String name;
    private boolean complete;
    private Integer version;

    public static TodoItem of(Faker faker) {
        TodoItem newItem = new TodoItem();
        newItem.setName("Watch  " + faker.breakingBad().episode());
        newItem.setComplete(false);
        return newItem;
    }
}
