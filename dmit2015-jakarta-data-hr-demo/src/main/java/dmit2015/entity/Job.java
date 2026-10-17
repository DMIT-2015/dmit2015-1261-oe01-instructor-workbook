package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "JOBS")
public class Job {
    @Id
    @Size(max = 10)
    @Column(name = "JOB_ID", nullable = false, length = 10)
    private String jobId;

    @Size(max = 35)
    @NotNull
    @Column(name = "JOB_TITLE", nullable = false, length = 35)
    private String jobTitle;

    @Column(name = "MIN_SALARY")
    private Integer minSalary;

    @Column(name = "MAX_SALARY")
    private Integer maxSalary;

    @OneToMany(mappedBy = "job")
    private Set<Employee> employees = new LinkedHashSet<>();

    @OneToMany(mappedBy = "job")
    private Set<JobHistory> jobHistories = new LinkedHashSet<>();


}