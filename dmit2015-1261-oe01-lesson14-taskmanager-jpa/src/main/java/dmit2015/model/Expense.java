package dmit2015.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "expense_id")
    private Long id;

    private String description;
    private BigDecimal amount;
    private LocalDate date;

    @Version
    private Integer version;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @PrePersist
    void onPrepersist() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        updateTime = now;
    }

    @PreUpdate
    void onPreUpdate() {
        updateTime = LocalDateTime.now();
    }

    public static Expense of(Faker faker) {
        Expense newExpense = new Expense();
        newExpense.setDescription(faker.breakingBad().episode());
        newExpense.setAmount(BigDecimal.valueOf(faker.number().numberBetween(1.00,99.99)));
        newExpense.setDate(LocalDate.now().minusDays(faker.number().numberBetween(1,7)));
        return newExpense;
    }


}
