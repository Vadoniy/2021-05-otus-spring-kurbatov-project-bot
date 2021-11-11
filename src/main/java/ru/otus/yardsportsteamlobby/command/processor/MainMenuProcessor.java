package ru.otus.yardsportsteamlobby.command.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface MainMenuProcessor {

    SendMessage process(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text);
}
