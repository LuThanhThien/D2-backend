package com.dainam.D2.models.user;

import com.dainam.D2.models.auth.Role;
import com.dainam.D2.models.auth.Token;
import com.dainam.D2.models.global.DataStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // USER INFORMATION
    private String fullName;

    private LocalDate dob;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    @Embedded
    private UserProfile profile;

    // SECURITY
    private String username;

    private String password;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DataStatus dataStatus = DataStatus.ACTIVE;

    @Builder.Default
    private LocalDateTime createdDatetime = LocalDateTime.now();

    private LocalDateTime lastLoginDatetime;

    private LocalDateTime lastChangePasswordDatetime;

    @Builder.Default
    @Column(name = "tokens")
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private Set<Token> tokens = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //== Association assist method ==//
    public void addToken(Token token) {
        this.tokens.add(token);
        token.setUser(this);
    }

    public void deleteTokens(Set<Token> tokens) {
        Arrays.asList(tokens.toArray()).forEach(this.tokens::remove);
    }

    public void updateLastLogin() {
        this.lastLoginDatetime = LocalDateTime.now();
    }

    public void updateLastChangePassword() {
        this.lastChangePasswordDatetime = LocalDateTime.now();
    }
}


