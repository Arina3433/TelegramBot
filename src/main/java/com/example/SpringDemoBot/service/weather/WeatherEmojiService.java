package com.example.SpringDemoBot.service.weather;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WeatherEmojiService {

    private static final Map<String, String> ICON_EMOJI_MAP = Map.ofEntries(
            Map.entry("01d", "☀️"),
            Map.entry("01n", "🌙"),
            Map.entry("02d", "🌤"),
            Map.entry("02n", "☁️🌙"),
            Map.entry("03d", "☁️"),
            Map.entry("03n", "☁️"),
            Map.entry("04d", "☁️☁️"),
            Map.entry("04n", "☁️☁️"),
            Map.entry("09d", "🌧"),
            Map.entry("09n", "🌧"),
            Map.entry("10d", "🌦"),
            Map.entry("10n", "🌧🌙"),
            Map.entry("11d", "⛈"),
            Map.entry("11n", "⛈🌙"),
            Map.entry("13d", "❄️"),
            Map.entry("13n", "❄️🌙"),
            Map.entry("50d", "🌫"),
            Map.entry("50n", "🌫🌙")
    );

    public String getEmoji(String iconCode) {
        return ICON_EMOJI_MAP.getOrDefault(iconCode, "❓");
    }
}
