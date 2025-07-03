package com.example.SpringDemoBot.service;

import com.example.SpringDemoBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private static final String SMILE = "\uD83D\uDE0A";
    private static final String SMILE_WITH_TEAR = "\uD83E\uDD72";
    private static final String SAD_AND_WORRIED = "\uD83D\uDE25";
    private static final String CRYING_FACE = "\uD83D\uDE22";

    private static final String HELP_TEXT = "Я даже не знаю чем вам можно помочь" + SMILE_WITH_TEAR +
            "\n\nНо этот бот пока просто хрени пишет" +
            "\n\nНажмите на /start чтобы поздороваться" +
            "\nНажмите на /my_data и должна появиться информация о тебе, но ее пока нет " + SAD_AND_WORRIED +
            "\nНажмите на /delete_data и можно удалить информацию о себе, но такого пока тоже нет " + SAD_AND_WORRIED +
            "\nНажмите на /settings и можно изменить настройки бота" +
            "\n\nПотом добавлю какую-нибудь обратную связь " + SMILE;

    private static final String SETTINGS_TEXT = "Нету тут ничего настраивать, ну по крайней мере пока";

    private final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Поздороваться"));
        listOfCommands.add(new BotCommand("/my_data", "Посмотреть информацию о себе"));
        listOfCommands.add(new BotCommand("/delete_data", "Удалить информацию о себе"));
        listOfCommands.add(new BotCommand("/help", "Помощь"));
        listOfCommands.add(new BotCommand("/settings", "Настроечки"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Не удалось загрузить команды: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                case "/settings":
                    sendMessage(chatId, SETTINGS_TEXT);
                    break;

                default:
                    sendMessage(chatId, "Такая команда пока не поддерживается " + CRYING_FACE);
                    log.info("Unknown message from user: {}\n" +
                            "Text: {}", update.getMessage().getChat().getFirstName(), messageText);
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет " + name + ", рад тебя видеть!";
        log.info("Replied to user: {}\nAnswer: {}", chatId, answer);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while sending command: {}", e.getMessage());
        }
    }
}
