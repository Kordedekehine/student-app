package com.studentunite.studentsapp.profile;

import com.studentunite.studentsapp.AppUser.AppUser;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000000) // 5MB
    private byte[] profilePicture;

    @Column(nullable = false, length = 5000000) // 5MB
    private byte[] backgroundPicture;

    private String country;

    private String department;

    private String url;

    private String bio;

    private String username;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "app_user_id")
    @ToString.Exclude
    private AppUser appUser;
}
