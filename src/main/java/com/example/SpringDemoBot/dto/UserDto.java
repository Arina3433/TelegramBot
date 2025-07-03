package com.example.SpringDemoBot.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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
                "Имя: " + firstName + "\n" +
                "Фамилия: " + lastName + "\n" +
                "Ник в Telegram: " + userName + "\n" +
                "Дата регистрации в боте: " + registeredAt;
    }
}
