package SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
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
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "fk_tour_id", nullable = false)
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "fk_customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "fk_employee_id", nullable = false)
    private Employee employee;

    @Column(name = "value", nullable = false)
    private double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.Pending;

    @Column(name = "Active")
    private boolean active;
}
