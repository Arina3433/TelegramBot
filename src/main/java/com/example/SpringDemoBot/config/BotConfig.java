package com.example.SpringDemoBot.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BotConfig {
    private final String botName;
    private final String token;

    public BotConfig() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        this.botName = dotenv.get("BOT_NAME");
        this.token = dotenv.get("BOT_TOKEN");

        if (botName == null || token == null) {
            throw new IllegalStateException("BOT_NAME or BOT_TOKEN is not set in .env");
        }
    }
}
