package dmit2015.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemDto implements Serializable {

    private Long id;

    @NotBlank(message = "Name value cannot be blank.")
    private String name;

    private boolean complete;

    private Integer version;

}