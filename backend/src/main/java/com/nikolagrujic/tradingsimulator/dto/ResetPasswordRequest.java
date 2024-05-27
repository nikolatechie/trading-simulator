package com.nikolagrujic.tradingsimulator.dto;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotNull
    @NotEmpty(message = "The password must not be empty!")
    @Size(min = Constants.PASSWORD_MIN_LENGTH, message = "The password must consist of at least 8 characters.")
    private String newPassword;

    @NotNull
    @NotEmpty(message = "The password must not be empty!")
    @Size(min = Constants.PASSWORD_MIN_LENGTH, message = "The password must consist of at least 8 characters.")
    private String newPasswordRepeat;

    @NotNull
    @NotEmpty(message = "The token must not be empty!")
    private String token;
}