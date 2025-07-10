package com.example.SpringDemoBot.service.weather;

import com.example.SpringDemoBot.dto.weather.WeatherResponseDto;
import com.example.SpringDemoBot.formatter.WeatherFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private static final String API_KEY = Dotenv.load().get("WEATHER_API_KEY");
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=ru&appid=%s";

    private final WeatherFormatter weatherFormatter;

    public String getWeather(String city) {
        try {
            String url = String.format(WEATHER_URL, city, API_KEY);

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                ObjectMapper mapper = new ObjectMapper();
                WeatherResponseDto dto = mapper.readValue(reader, WeatherResponseDto.class);

                return weatherFormatter.format(dto);
            }

        } catch (IOException e) {
            log.error("Error getting weather: {}", e.getMessage());
            return "Не удалось получить погоду. Попробуйте позже.";
        }
    }
}
