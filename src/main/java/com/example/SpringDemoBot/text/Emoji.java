package com.example.SpringDemoBot.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Emoji {
    SMILE("\uD83D\uDE0A"),
    SMILE_WITH_TEAR("\uD83E\uDD72"),
    SAD_AND_WORRIED("\uD83D\uDE25"),
    CRYING_FACE("\uD83D\uDE22"),
    THINKING_FACE("\uD83E\uDD14"),
    UPSIDE_DOWN_FACE("\uD83D\uDE43"),

    SPARKLES("✨"),
    KITTEN("\uD83D\uDC31");

    private final String symbol;

    @Override
    public String toString() {
        return symbol;
    }
}

