package com.example.SpringDemoBot.service.news;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class NakedScienceNewsService {

    private static final String BASE_URL = "https://naked-science.ru/article/page/";
    private static final int MAX_PAGES = 1945;

    private final Random random = new Random();

    public String getRandomArticleUrl() {
        int page = random.nextInt(MAX_PAGES) + 1;
        String url = BASE_URL + page + "/";

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            Elements newsItems = doc.select("div.news-item");

            List<String> newsList = newsItems.stream()
                    .map(news -> {
                        Element titleLink = news.selectFirst("div.news-item-title h3 a");

                        String title = titleLink != null ? titleLink.text().trim() : "Без заголовка";
                        String link = titleLink != null ? titleLink.absUrl("href") : "Без ссылки";

                        return String.format("%s\n%s", title, link);
                    })
                    .toList();

            if (newsList.isEmpty()) {
                return "Не удалось найти новости 😔";
            }

            String randomNews = newsList.get(new Random().nextInt(newsList.size()));
            log.info("User get article: {}", randomNews);
            return "🧠 Вот интересная статья :\n" + randomNews;

        } catch (Exception e) {
            log.error("Error getting article: {}", e.getMessage());
            return "Произошла ошибка при получении статьи. Попробуйте позже.";
        }
    }
}
