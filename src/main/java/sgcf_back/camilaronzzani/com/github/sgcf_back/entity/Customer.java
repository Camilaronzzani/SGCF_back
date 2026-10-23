package sgcf_back.camilaronzzani.com.github.sgcf_back.entity;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.CountryCustomer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
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

    @Column(name = "cnpj" , length = 15, unique = true)
    private String cnpj;

    @Column(name = "cpf" , length = 11, unique = true )
    private String cpf;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "clientes_languages", joinColumns = @JoinColumn(name = "fk_clientes_id"))
    @Column(name = "language")
    private List<Language> languageSpeak;

    @Enumerated(EnumType.STRING)
    @Column(name = "country_of_customer")
    private CountryCustomer countryCustomer;

    @Column(name = "user_email", nullable = false, length = 100, unique = true)
    private String email;

    @OneToMany(mappedBy = "customer")
    private List<Reservation> reservations;

    @Column(name = "Active")
    private boolean active;

    @PrePersist
    private void prePersist() {
        if (languageSpeak == null) {
            languageSpeak = new ArrayList<>();
        }
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
    }
}
