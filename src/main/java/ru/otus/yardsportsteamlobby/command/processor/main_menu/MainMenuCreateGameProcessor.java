package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.MainMenuProcessor;
import ru.otus.yardsportsteamlobby.service.GameService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
public class MainMenuCreateGameProcessor implements MainMenuProcessor {

    private final GameService gameService;

    @Override
    public SendMessage process(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text) {
        return gameService.createGame(chatId, userId, text);
    }
}
