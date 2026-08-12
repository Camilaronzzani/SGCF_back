package Entity;

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
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf", nullable = false , length = 11)
    private String cpf;

    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "languages_funcionario" , joinColumns = @JoinColumn(name = "fk_funcionario_id")
    )
    @Column(name = "language")
    private List<Language> languagesSpoken = new ArrayList<>();

    @Column(name = "Active")
    private boolean active ;
}
