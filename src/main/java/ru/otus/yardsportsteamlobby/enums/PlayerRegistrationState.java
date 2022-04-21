package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.DeletePlayerProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyNameProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyNumberProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyPhoneProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyPositionProcessor;

@Getter
@RequiredArgsConstructor
public enum PlayerRegistrationState {

    EMPTY_NAME(EmptyNameProcessor.class),

    EMPTY_PHONE(EmptyPhoneProcessor.class),

    EMPTY_POSITION(EmptyPositionProcessor.class),

    EMPTY_NUMBER(EmptyNumberProcessor.class),

    DELETE(DeletePlayerProcessor.class);

    private final Class<? extends TelegramMessageProcessor> processor;
}
