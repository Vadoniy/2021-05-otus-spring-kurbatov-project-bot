package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.GameSelectStateProcessor;

@Getter
@RequiredArgsConstructor
public enum CallbackQuerySelect {

    SELECTED_GAME_(GameSelectStateProcessor.class);

    private final Class<? extends TelegramMessageProcessor> processor;
}
