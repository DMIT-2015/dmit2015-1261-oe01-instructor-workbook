package dmit2015.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Entity
@Data
@NoArgsConstructor
public class Task {

    @Id
    private String id;

    @NotBlank(message = "Description is required")
    @Size(min=3, max=64,
            message = "Task description must contain {min} and {max} characters in length.")
    private String description;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    private boolean done;

    @Version
    private Integer version;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        updateTime = now;
    }

    @PreUpdate
    void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // Copy constructor
    public Task(Task other) {
        this.id = other.getId();
        this.description = other.getDescription();
        this.priority = other.getPriority();
        this.done = other.isDone();
    }

    // Static copyOf method
    public static Task copyOf(Task other) {
        return new Task(other);
    }

    // Static of method to return a new instance with fake data
    public static Task of(Faker faker) {
        Task newTask = new Task();
        newTask.setId(UUID.randomUUID().toString());
        newTask.setDescription("Watch " + faker.movie().name());
        TaskPriority[] possiblePriorities = TaskPriority.values();
        // Generate an index to pick from the array
        int randomIndex = RandomGenerator.getDefault().nextInt(0, possiblePriorities.length);
        newTask.setPriority(possiblePriorities[randomIndex]);
        newTask.setDone(false);
        return newTask;
    }
}
