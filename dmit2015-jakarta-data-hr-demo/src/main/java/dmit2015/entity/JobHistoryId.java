package dmit2015.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class JobHistoryId implements Serializable {
    @Serial
    private static final long serialVersionUID = -6871442616824564831L;
    @NotNull
    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Integer employeeId;

    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;


}