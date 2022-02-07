package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.*;

@Getter
@RequiredArgsConstructor
public enum PlayerRegistrationState {

    EMPTY_NAME(EmptyNameProcessor.class),

    EMPTY_PHONE(EmptyPhoneProcessor.class),

    EMPTY_POSITION(EmptyPositionProcessor.class),

    EMPTY_NUMBER(EmptyNumberProcessor.class),

    DELETE(DeletePlayerProcessor.class);

    private final Class<? extends PlayerMenuProcessor> processor;
}
