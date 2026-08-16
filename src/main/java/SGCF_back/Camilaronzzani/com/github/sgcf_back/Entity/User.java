package SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "user_name" , nullable = false , length = 35 , unique = true)
    private String userName;

    @Column(name = "user_password" , nullable = false)
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false)
    private Permission permission;

    @Column(name = "user_email", nullable = false, length = 100, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "fk_employee_id")
    private Employee employee;

    @Column(name = "Active")
    private boolean active ;
}
