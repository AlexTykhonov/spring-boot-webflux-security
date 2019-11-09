package com.l2d.tuto.springbootwebfluxsecurity.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.Authentication;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Document(collection = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(unique = true)
    private String login;

    @Size(min = 60, max = 60)
    private String password;

    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();

    private List<Moto> motos;

    public static Optional<User> currentUser (Authentication auth) {
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User) // User is your user type that implements UserDetails
                return Optional.of((User) principal);
        }
        return Optional.empty();
    }
}
