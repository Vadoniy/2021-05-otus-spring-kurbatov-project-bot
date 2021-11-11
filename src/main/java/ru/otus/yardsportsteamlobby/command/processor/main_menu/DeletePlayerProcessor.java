package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.MainMenuProcessor;
import ru.otus.yardsportsteamlobby.enums.YesNoSelect;
import ru.otus.yardsportsteamlobby.service.PlayerService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
public class DeletePlayerProcessor implements MainMenuProcessor {

    private final PlayerService playerService;

    @Override
    public SendMessage process(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text) {
        return playerService.deletePlayer(chatId, userId, YesNoSelect.valueOf(text));
    }
}
