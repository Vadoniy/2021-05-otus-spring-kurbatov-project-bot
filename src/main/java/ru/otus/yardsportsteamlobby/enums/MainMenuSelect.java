package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.MainMenuProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.*;

@Getter
@RequiredArgsConstructor
public enum MainMenuSelect {

    START("/start", "main.menu.greetings", MainMenuKeyboardProcessor.class),

    REGISTER("REGISTER", "main.menu.register", RegisterProcessor.class),

    SIGN_UP_FOR_GAME("SIGN_UP_FOR_GAME", "main.menu.sign-up-for-game", SignUpForGameProcessor.class),

    CREATE_GAME("CREATE_GAME", "main.menu.create-game", MainMenuCreateGameProcessor.class),

    DELETE_PLAYER("DELETE_PLAYER", "main.menu.delete-player-data", MainMenuDeletePlayerProcessor.class);

    private final String name;

    private final String message;

    private final Class<? extends MainMenuProcessor> processor;

    public static Class<? extends MainMenuProcessor> getMainMenuProcessor(String message) {
        for (MainMenuSelect mainMenuSelect : MainMenuSelect.values()) {
            if (mainMenuSelect.name.equals(message)) {
                return mainMenuSelect.processor;
            }
        }
        return RegisterProcessor.class;
    }
}
