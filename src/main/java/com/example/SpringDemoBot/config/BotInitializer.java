package com.example.SpringDemoBot.config;

import com.example.SpringDemoBot.service.TelegramBotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BotInitializer {
    TelegramBotService bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
            setBotCommands();
            log.info("Bot successfully registered");
        } catch (TelegramApiException e) {
            log.error("\nError initializing bot: {}", e.getMessage());
        }
    }

    private void setBotCommands() {
        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Поздороваться"),
                new BotCommand("/view_motivational_pic", "Посмотреть мотивирующую картинку"),
                new BotCommand("/app", "Посмотреть что можно здесь делать"),
                new BotCommand("/my_data", "Посмотреть информацию о себе"),
                new BotCommand("/help", "Помощь"),
                new BotCommand("/settings", "Настроечки")
        );

        try {
            bot.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Failed to set commands: {}", e.getMessage());
        }
    }
}
