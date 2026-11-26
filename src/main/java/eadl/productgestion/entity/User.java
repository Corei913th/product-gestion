package eadl.productgestion.entity;

import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String userName;

    @Column(nullable = false, unique = true, length = 120)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserAccountStatus userAccountStatus;
}
