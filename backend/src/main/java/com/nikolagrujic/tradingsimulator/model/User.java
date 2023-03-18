package com.nikolagrujic.tradingsimulator.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    @NotBlank(message = "First name must not be empty!")
    private String firstName;

    @Column
    @NotNull
    @NotBlank(message = "Last name must not be empty!")
    private String lastName;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "The email must not be empty!")
    @Email(message = "Wrong email format.")
    private String email;

    @Column
    @NotNull
    @NotEmpty(message = "The password must not be empty!")
    @Size(min = 8, message = "The password length must consist of at least 8 characters.")
    private String password;
}
