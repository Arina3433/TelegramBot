package com.example.SpringDemoBot.formatter;

import com.example.SpringDemoBot.dto.weather.WeatherResponseDto;
import com.example.SpringDemoBot.service.weather.WeatherEmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class WeatherFormatter {

    private final WeatherEmojiService emojiService;

    public String format(WeatherResponseDto dto) {
        StringBuilder sb = new StringBuilder();
        var weatherInfo = (dto.getWeather() != null && !dto.getWeather().isEmpty()) ? dto.getWeather().get(0) : null;

        sb.append("🌍 Город: ").append(dto.getName() != null ? dto.getName() : "неизвестен").append("\n\n");

        if (weatherInfo != null) {
            String emoji = emojiService.getEmoji(weatherInfo.getIcon());
            sb.append(emoji).append(" Погода: ").append(weatherInfo.getDescription()).append("\n\n");
        }

        var main = dto.getMain();
        if (main != null) {
            double pressureMmHg = main.getPressure() * 0.75006;
            sb.append("🌡 Температура: ").append(String.format("%.1f°C", main.getTemp())).append("\n\n")
                    .append("🤔 Ощущается как: ").append(String.format("%.1f°C", main.getFeelsLike())).append("\n\n")
                    .append("💧 Влажность: ").append(main.getHumidity()).append("%\n\n")
                    .append("🔽 Давление: ").append(String.format("%.1f мм рт. ст.", pressureMmHg)).append("\n\n");
        }

        var wind = dto.getWind();
        if (wind != null) {
            sb.append("🌬 Ветер: ").append(String.format("%.1f м/с", wind.getSpeed()))
                    .append(" (направление: ").append(wind.getDeg()).append("°)").append("\n\n");
        }

        var sys = dto.getSys();
        if (sys != null) {
            sb.append("🌅 Восход солнца: ").append(formatUnix(sys.getSunrise(), dto.getTimezone())).append("\n\n");
            sb.append("🌇 Закат солнца: ").append(formatUnix(sys.getSunset(), dto.getTimezone())).append("\n\n");
        }

        return sb.toString();
    }

    private String formatUnix(long unixSeconds, int timezoneSeconds) {
        Instant instant = Instant.ofEpochSecond(unixSeconds);
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneSeconds);
        LocalDateTime time = LocalDateTime.ofInstant(instant, offset);
        return time.toLocalTime().withSecond(0).toString(); // hh:mm
    }
}
