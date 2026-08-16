package SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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

    @Column(name = "day_of_birth")
    private LocalDate dayOfBirth;


    @Column(name = "Active")
    private boolean active ;
}
