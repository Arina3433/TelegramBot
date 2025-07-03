package com.example.SpringDemoBot.service;

import com.example.SpringDemoBot.config.BotConfig;
import com.example.SpringDemoBot.dto.UserDto;
import com.example.SpringDemoBot.model.entity.User;
import com.example.SpringDemoBot.model.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.example.SpringDemoBot.text.Emoji.*;

@Service
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {
    private final BotConfig config;
    private final UserRepository userRepository;

    private static final String HELP_TEXT = "Я даже не знаю чем вам можно помочь " + SMILE_WITH_TEAR +
            "\n\n\nНо этот бот пока просто хрени пишет" +
            "\n\nНажмите на /start чтобы поздороваться" +
            "\n\nНажмите на /view_motivational_pic чтобы посмотреть мотивирующую картинку" +
            "\n\nНажмите на /my_data и появиться информация о тебе" +
            "\n\nНажмите на /delete_data и можно удалить информацию о себе" +
            "\n\nНажмите на /settings и можно изменить настройки бота" +
            "\n\n\nПотом добавлю какую-нибудь обратную связь " + SMILE;

    private static final String SETTINGS_TEXT = "Нету тут ничего настраивать, ну по крайней мере пока";

    private static final String UNREGISTERED = "Почему-то ваших данных у нас нет " + THINKING_FACE +
            "\nНажмите сюда /start, поздороваемся еще раз";

    private static final String DELETE_DATA = "Ничего не удаляется, все будет хранится " + UPSIDE_DOWN_FACE;

    public TelegramBotService(BotConfig config, UserRepository userRepository) {
        this.config = config;
        this.userRepository = userRepository;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Поздороваться"));
        listOfCommands.add(new BotCommand("/view_motivational_pic", "Посмотреть мотивирующую картинку"));
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
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                    case "/view_motivational_pic":
                        sendMotivationalPicture(chatId);
                        break;

                case "/my_data":
                    getUserInfo(chatId);
                    break;

                case "/delete_data":
                    sendMessage(chatId, DELETE_DATA);
                    break;

                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                case "/settings":
                    sendMessage(chatId, SETTINGS_TEXT);
                    break;

                default:
                    sendMessage(chatId, "Такая команда пока не поддерживается " + CRYING_FACE);
                    log.info("\nUnknown message from user: {}\n" +
                            "Text: {}", update.getMessage().getChat().getFirstName(), messageText);
            }
        }
    }

    private void sendMotivationalPicture(Long chatId) {
        try (InputStream imageStream = getClass().getResourceAsStream("/images/cat.jpg")) {
            if (imageStream == null) {
                log.error("Image not found");
                sendMessage(chatId, "Картинка не нашлась " + SAD_AND_WORRIED);
                return;
            }

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId);
            photo.setPhoto(new InputFile(imageStream, "cat.jpg"));
            photo.setCaption("Вот вам котик " + KITTEN);

            execute(photo);
            log.info("Send motivational picture");
        } catch (Exception e) {
            log.error("Send image error: {}", e.getMessage());
        }
    }

    private void getUserInfo(long chatId) {
        User user = userRepository.findById(chatId).orElse(null);
        if (user != null) {
            UserDto userDto = UserDto.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userName(user.getUserName())
                    .registeredAt(user.getRegisteredAt())
                    .build();

            sendMessage(chatId, userDto.toString());
        } else {
            sendMessage(chatId, UNREGISTERED);
        }
    }

    private void registerUser(Message message) {
        var chatId = message.getChatId();
        if (userRepository.findById(chatId).isEmpty()) {
            var chat = message.getChat();

            User user = User.builder()
                    .chatId(chatId)
                    .firstName(chat.getFirstName())
                    .lastName(chat.getLastName())
                    .userName(chat.getUserName())
                    .registeredAt(LocalDateTime.now(ZoneOffset.UTC))
                    .build();

            userRepository.save(user);
            log.info("\nUser registered: {}", user);
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет " + name + ", рад тебя видеть!";

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
            log.info("\nReplied to user: {}\nAnswer: {}", chatId, textToSend);
        } catch (TelegramApiException e) {
            log.error("\nError while sending command: {}", e.getMessage());
        }
    }
}
