package com.example.SpringDemoBot.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.SpringDemoBot.text.Emoji.SPARKLES;

@Data
@Builder
public class UserDto {
    private String firstName;
    private String lastName;
    private String userName;
    private LocalDateTime registeredAt;

    @Override
    public String toString() {
        return "Информация о вас " + SPARKLES + "\n" +
                "Имя: " + Optional.ofNullable(firstName).orElse("неизвестно") + "\n" +
                "Фамилия: " + Optional.ofNullable(lastName).orElse("неизвестно") + "\n" +
                "Ник в Telegram: " + Optional.ofNullable(userName).orElse("неизвестно") + "\n" +
                "Дата регистрации в боте: " + registeredAt;
    }
}
