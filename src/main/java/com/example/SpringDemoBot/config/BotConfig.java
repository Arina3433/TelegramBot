package com.example.SpringDemoBot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BotConfig {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

//    private final String botName = System.getenv("BOT_NAME");
//    private final String token = System.getenv("BOT_TOKEN");
}
