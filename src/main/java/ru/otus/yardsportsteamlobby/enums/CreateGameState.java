package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyCapacityProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyDateProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyTeamAProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyTeamBProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyTimeProcessor;

@Getter
@RequiredArgsConstructor
public enum CreateGameState {

    EMPTY_DATE(EmptyDateProcessor.class),

    EMPTY_TIME(EmptyTimeProcessor.class),

    EMPTY_CAPACITY(EmptyCapacityProcessor.class),

    EMPTY_TEAM_1_NAME(EmptyTeamAProcessor.class),

    EMPTY_TEAM_2_NAME(EmptyTeamBProcessor.class);

    private final Class<? extends TelegramMessageProcessor> processor;
}
