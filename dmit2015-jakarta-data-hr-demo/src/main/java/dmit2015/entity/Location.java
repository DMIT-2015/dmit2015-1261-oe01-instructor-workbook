package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "LOCATIONS")
public class Location {
    @Id
    @Column(name = "LOCATION_ID", nullable = false)
    private Short id;

    @Size(max = 40)
    @Column(name = "STREET_ADDRESS", length = 40)
    private String streetAddress;

    @Size(max = 12)
    @Column(name = "POSTAL_CODE", length = 12)
    private String postalCode;

    @Size(max = 30)
    @NotNull
    @Column(name = "CITY", nullable = false, length = 30)
    private String city;

    @Size(max = 25)
    @Column(name = "STATE_PROVINCE", length = 25)
    private String stateProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "COUNTRY_ID")
    private Country country;

    @OneToMany(mappedBy = "location")
    private Set<Department> departments = new LinkedHashSet<>();


}