package Entity;

import Entity.Enum.CountryTour;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "price")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column (name = "country_of_tour")
    private CountryTour countryTour;

    @Column (name = "km_of_tour")
    private Long kmOftour;

    @Column (name = "name")
    private  String nameOfTour;

    @Column(name = "locations")
    private  String locations;

    @Column(name = "Active")
    private boolean active ;

}
