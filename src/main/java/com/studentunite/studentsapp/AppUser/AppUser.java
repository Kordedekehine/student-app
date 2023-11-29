package com.studentunite.studentsapp.AppUser;



import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;


@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AppUser  {  //implements UserDetails

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long appUserId;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email
    @NotEmpty(message = "Email is required")
    private String email;

    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;

    private Instant created;

    private boolean enabled = false;

    private boolean locked = false;

    private String validationToken;

    private String resetPasswordToken;

    public String getEmail() {
        return email;
    }

    public AppUserRole getAppUserRole() {
        return appUserRole;
    }

    public Instant getCreated() {
        return created;
    }

}
