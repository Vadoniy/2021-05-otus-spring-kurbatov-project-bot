package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum MainMenuButtonName {

    GREETINGS("main.menu.greetings", BotState.MAIN_MENU),

    REGISTER("main.menu.register", BotState.REGISTER),

    ru("main.menu.locale.ru", BotState.RU),

    en("main.menu.locale.en", BotState.EN),

    SIGN_UP_FOR_GAME("main.menu.sign-up-for-game", BotState.SIGN_UP_FOR_GAME),

    CREATE_GAME("main.menu.create-game", BotState.CREATE_GAME),

    DELETE_PLAYER("main.menu.delete-player-data", BotState.DELETE_PLAYER);

    private final String buttonNamePath;

    private final BotState botState;

    public static MainMenuButtonName fromValue(String value) {
        for (MainMenuButtonName mainMenuButtonName : MainMenuButtonName.values()) {
            if (mainMenuButtonName.getButtonNamePath().equals(value)) {
                return mainMenuButtonName;
            }
        }
        return null;
    }

    public static MainMenuButtonName fromLocale(String value) {
        for (MainMenuButtonName mainMenuButtonName : List.of(ru, en)) {
            if (mainMenuButtonName.toString().equals(value)) {
                return mainMenuButtonName;
            }
        }
        return null;
    }
}
