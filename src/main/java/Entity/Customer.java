package Entity;

import Entity.Enum.CountryCostumer;
import Entity.Enum.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "cnpj" , length = 15)
    private String cnpj;

    @Column(name = "cpf" , length = 11 )
    private String cpf;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "clientes_languages", joinColumns = @JoinColumn(name = "fk_clientes_id"))
    @Column(name = "language")
    private List<Language> languageSpeak = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "country_of_customer")
    private CountryCostumer countryOfCostumer;

    @Column(name = "Active")
    private boolean active ;
}
