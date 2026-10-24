package sgcf_back.camilaronzzani.com.github.sgcf_back.entity;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
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
    private Status status;

    @ManyToMany
    @JoinTable(
            name = "reservations_customer",
            joinColumns = @JoinColumn(name = "reservations_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> customersNotPaying;

    @Column(name = "Active")
    private boolean active;

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = Status.PENDING;
        }
        if (customersNotPaying == null) {
            customersNotPaying = new ArrayList<>();
        }
    }
}
