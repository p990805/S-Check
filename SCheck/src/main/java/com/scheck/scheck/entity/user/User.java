package com.scheck.scheck.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @Column(nullable = false)
    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private AuthType authType = AuthType.EMAIL;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Boolean isActive = true;

    @OneToMany (mappedBy = "user" , cascade = CascadeType.ALL)
    private List<OAuthAccount> oAuthAccounts = new ArrayList<>();

}
