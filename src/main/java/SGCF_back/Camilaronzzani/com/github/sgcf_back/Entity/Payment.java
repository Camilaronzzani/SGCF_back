package SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.Pending;

    @ManyToOne
    @JoinColumn(name = "fk_customer_id", nullable = false)
    private Customer customer;

    @Column(name = "total_account")
    private double totalAccount;

    @Column(name = "Active")
    private boolean active;
}
