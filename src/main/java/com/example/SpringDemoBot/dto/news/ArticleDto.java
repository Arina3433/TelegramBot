package com.example.SpringDemoBot.dto.news;

import lombok.Data;

@Data
public class ArticleDto {
    private String title;
    private String link;
    private String description;

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s", title, description, link);
    }
}
