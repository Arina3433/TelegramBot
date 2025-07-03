package com.example.SpringDemoBot.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BotConfig {
    private final String botName = System.getenv("BOT_NAME");
    private final String token = System.getenv("BOT_TOKEN");
}
