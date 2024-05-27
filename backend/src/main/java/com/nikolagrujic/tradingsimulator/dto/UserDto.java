package com.nikolagrujic.tradingsimulator.dto;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull
    @NotBlank(message = "First name must not be empty!")
    private String firstName;

    @NotNull
    @NotBlank(message = "Last name must not be empty!")
    private String lastName;

    @NotNull
    @NotEmpty(message = "The password must not be empty!")
    @Size(min = Constants.PASSWORD_MIN_LENGTH, message = "The password must consist of at least 8 characters.")
    private String currentPassword;

    private String newPassword;
    private String newPasswordRepeat;
}