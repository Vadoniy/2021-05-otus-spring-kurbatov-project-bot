package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.MainMenuCreateGameProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.MainMenuDeletePlayerProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.MainMenuKeyboardProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.RegisterProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.SignUpForGameProcessor;

@Getter
@RequiredArgsConstructor
public enum MainMenuSelect {

    MAIN_MENU(":v:", "main.menu.greetings", MainMenuKeyboardProcessor.class),

    REGISTER(":heavy_check_mark:", "main.menu.register", RegisterProcessor.class),

    RU(":ru:", "main.menu.locale.ru", MainMenuKeyboardProcessor.class),

    EN(":gb:", "main.menu.locale.en", MainMenuKeyboardProcessor.class),

    SIGN_UP_FOR_GAME(":heavy_plus_sign:", "main.menu.sign-up-for-game", SignUpForGameProcessor.class),

    CREATE_GAME(":ice_hockey:", "main.menu.create-game", MainMenuCreateGameProcessor.class),

    DELETE_PLAYER(":x:", "main.menu.delete-player-data", MainMenuDeletePlayerProcessor.class);

    private final String emoji;

    private final String message;

    private final Class<? extends TelegramMessageProcessor> processor;

    public static Class<? extends TelegramMessageProcessor> getMainMenuProcessor(String message) {
        for (MainMenuSelect mainMenuSelect : MainMenuSelect.values()) {
            if (message.contains(mainMenuSelect.emoji)) {
                return mainMenuSelect.processor;
            }
        }
        return RegisterProcessor.class;
    }

    public static MainMenuSelect resolveByEmoji(String emojiShortCode) {
        for (MainMenuSelect mainMenuSelect : MainMenuSelect.values()) {
            if (mainMenuSelect.getEmoji().equals(emojiShortCode)) {
                return mainMenuSelect;
            }
        }
        return MAIN_MENU;
    }
}
