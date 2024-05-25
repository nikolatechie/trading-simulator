package com.nikolagrujic.tradingsimulator.model;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank(message = "First name must not be empty!")
    private String firstName;

    @NotNull
    @NotBlank(message = "Last name must not be empty!")
    private String lastName;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "The email must not be empty!")
    @Email(message = "Wrong email format.")
    private String email;

    @NotNull
    @NotEmpty(message = "The password must not be empty!")
    @Size(min = Constants.PASSWORD_MIN_LENGTH, message = "The password length must consist of at least 8 characters.")
    private String password;

    @Column(columnDefinition = "boolean default false")
    private boolean emailVerified;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private EmailVerificationToken emailVerificationToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Portfolio portfolio;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    private LocalDate verificationDate;

    @ManyToMany(mappedBy = "likedBy")
    private Set<NewsArticle> likedNewsArticles = new HashSet<>();

    public boolean hasLikedArticle(NewsArticle article) {
        return likedNewsArticles.contains(article);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsArticleComment> comments = new ArrayList<>();
}