package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "COUNTRIES")
public class Country {
    @Id
    @Size(max = 2)
    @Column(name = "COUNTRY_ID", nullable = false, length = 2)
    private String countryId;

    @Size(max = 60)
    @Column(name = "COUNTRY_NAME", length = 60)
    private String countryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "REGION_ID")
    private Region region;

    @OneToMany(mappedBy = "country")
    private Set<Location> locations = new LinkedHashSet<>();


}