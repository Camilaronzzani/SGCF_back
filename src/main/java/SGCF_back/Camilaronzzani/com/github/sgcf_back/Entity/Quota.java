package SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "target_value")
    private double targetValue;

    @ManyToOne
    @JoinColumn(name = "fk_employee_id")
    private Employee employee;

    @Column(name = "Active")
    private boolean active;
}
