package com.example.SpringDemoBot.text;

import static com.example.SpringDemoBot.text.Emoji.CRYING_FACE;

public class BotMessages {
    public static final String HELP_TEXT = "Я даже не знаю чем вам можно помочь " + Emoji.SMILE_WITH_TEAR +
            "\n\n\nНо этот бот пока просто хрени пишет" +
            "\n\nНажмите на /start чтобы поздороваться" +
            "\n\nНажмите на /view_motivational_pic чтобы посмотреть мотивирующую картинку" +
            "\n\nНажмите на /app чтобы посмотреть что можно делать в этом боте" +
            "\n\nНажмите на /my_data и появиться информация о тебе" +
            "\n\nНажмите на /settings и можно изменить настройки бота" +
            "\n\n\nПотом добавлю какую-нибудь обратную связь " + Emoji.SMILE;

    public static final String SETTINGS_TEXT = "Нету тут ничего настраивать, ну по крайней мере пока";

    public static final String UNREGISTERED = "Почему-то ваших данных у нас нет " + Emoji.THINKING_FACE +
            "\nНажмите сюда /start, поздороваемся еще раз";

    public static final String DEFAULT_MESSAGE = "Такая команда пока не поддерживается " + CRYING_FACE +
            "\nПопробуйте выбрать команду из меню";

    private BotMessages() {
    }
}
