package com.example.SpringDemoBot.service;

import com.example.SpringDemoBot.config.BotConfig;
import com.example.SpringDemoBot.dto.UserDto;
import com.example.SpringDemoBot.model.entity.User;
import com.example.SpringDemoBot.model.repo.UserRepository;
import com.example.SpringDemoBot.service.news.NakedScienceNewsService;
import com.example.SpringDemoBot.service.weather.WeatherService;
import com.example.SpringDemoBot.text.BotCallbackData;
import com.example.SpringDemoBot.text.BotMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.SpringDemoBot.text.Emoji.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {
    private final WeatherService weatherService;
    private final NakedScienceNewsService newsService;

    private final BotConfig config;
    private final UserRepository userRepository;

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

                case "/help":
                    createMessage(chatId, BotMessages.HELP_TEXT);
                    break;

                case "/settings":
                    createMessage(chatId, BotMessages.SETTINGS_TEXT);
                    break;

                case "/app":
                    showApp(chatId);
                    break;

                default:
                    createMessage(chatId, BotMessages.DEFAULT_MESSAGE);
                    log.info("""                            
                            Unknown message from user: {}
                            Text: {}""", update.getMessage().getChat().getFirstName(), messageText);
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackId = update.getCallbackQuery().getId();

            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackId);
            try {
                execute(answer);
            } catch (TelegramApiException e) {
                log.error("Ошибка при ответе на callback: {}", e.getMessage());
            }

            switch (callbackData) {
                case BotCallbackData.WEATHER:
                    String text = "Погоду в каком городе вы хотите посмотреть?";
                    List<List<Map.Entry<String, String>>> buttonRows = List.of(
                            List.of(
                                    createButtonTextCallbackEntry("Самара", BotCallbackData.SMR),
                                    createButtonTextCallbackEntry("Екатеринбург", BotCallbackData.EKB)
                            )
                    );
                    InlineKeyboardMarkup inlineKeyboardMarkup = createInlineMarkup(buttonRows);

                    createMessage(chatId, text, inlineKeyboardMarkup);
                    break;

                case BotCallbackData.NEWS:
                    createMessage(chatId, newsService.getRandomArticleUrl());
                    log.info("User {} get article", update.getCallbackQuery().getFrom().getFirstName());
                    break;

                case BotCallbackData.SMR:
                    createMessage(chatId, weatherService.getWeather("Samara"));
                    log.info("User {} get weather to Samara", update.getCallbackQuery().getFrom().getFirstName());
                    break;

                case BotCallbackData.EKB:
                    createMessage(chatId, weatherService.getWeather("Ekaterinburg"));
                    log.info("User {} get weather to EKB", update.getCallbackQuery().getFrom().getFirstName());
                    break;
            }
        }
    }

    private void showApp(long chatId) {
        List<List<Map.Entry<String, String>>> buttonRows = List.of(
                List.of(
                        createButtonTextCallbackEntry("Погода", BotCallbackData.WEATHER),
                        createButtonTextCallbackEntry("Какая-нибудь новость", BotCallbackData.NEWS)
                )
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineMarkup(buttonRows);
        createMessage(chatId, "Что хотите посмотреть?", inlineKeyboardMarkup);
    }

    private void sendMotivationalPicture(Long chatId) {
        try (InputStream imageStream = getClass().getResourceAsStream("/images/cat.jpg")) {
            if (imageStream == null) {
                log.error("Image not found");
                createMessage(chatId, "Картинка не нашлась " + SAD_AND_WORRIED);
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

            createMessage(chatId, userDto.toString());
        } else {
            createMessage(chatId, BotMessages.UNREGISTERED);
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

        createMessage(chatId, answer);
    }

    private void createMessage(long chatId, String textToSend, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        if (replyKeyboard != null) {
            message.setReplyMarkup(replyKeyboard);
        }

        sendMessage(message);
    }

    private void sendMessage(BotApiMethod<?> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("\nError while sending command: {}", e.getMessage());
        }
    }

    private void createMessage(long chatId, String textToSend) {
        createMessage(chatId, textToSend, null);
    }

    private static ReplyKeyboardMarkup createReplyMarkup(List<List<String>> buttonTextList) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (List<String> row : buttonTextList) {
            KeyboardRow keyboardRow = new KeyboardRow();

            keyboardRow.addAll(row);
            keyboardRows.add(keyboardRow);
        }

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    private static InlineKeyboardMarkup createInlineMarkup(List<List<Map.Entry<String, String>>> buttonRows) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for (List<Map.Entry<String, String>> row : buttonRows) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            for (Map.Entry<String, String> entry : row) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(entry.getKey());
                button.setCallbackData(entry.getValue());

                rowInline.add(button);
            }
            rowsInLine.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        return inlineKeyboardMarkup;
    }

    public static Map.Entry<String, String> createButtonTextCallbackEntry(String text, String callbackData) {
        return new AbstractMap.SimpleEntry<>(text, callbackData);
    }

}
