package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "REGIONS")
public class Region {
    @Id
    @Column(name = "REGION_ID", nullable = false)
    private Long id;

    @Size(max = 25)
    @Column(name = "REGION_NAME", length = 25)
    private String regionName;

    @OneToMany(mappedBy = "region")
    private Set<Country> countries = new LinkedHashSet<>();


}