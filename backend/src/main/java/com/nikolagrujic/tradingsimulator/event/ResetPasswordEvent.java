package com.nikolagrujic.tradingsimulator.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.mail.SimpleMailMessage;

@AllArgsConstructor
@Getter
public class ResetPasswordEvent {
    private final SimpleMailMessage simpleMailMessage;
}