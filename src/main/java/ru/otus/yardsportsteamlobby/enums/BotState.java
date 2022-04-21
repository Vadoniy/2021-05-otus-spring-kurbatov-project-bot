package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.DeleteOrNotPlayerProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.GameSelectStateProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.SelectDateProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.SelectPositionProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.SkipTeamsNameProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.TeamSelectStateProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyCapacityProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyDateProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyTeamAProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyTeamBProcessor;
import ru.otus.yardsportsteamlobby.command.processor.create_game.EmptyTimeProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.MainMenuCreateGameProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.MainMenuDeletePlayerProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.MainMenuKeyboardProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.RegisterProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.SignUpForGameProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.DeletePlayerProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyNameProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyNumberProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyPhoneProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.EmptyPositionProcessor;

@Getter
@RequiredArgsConstructor
public enum BotState {

    FIELD(SelectPositionProcessor.class),

    UNIQUE(SelectPositionProcessor.class),

    SURE_TO_DELETE_PLAYER(DeleteOrNotPlayerProcessor.class),

    NOT_SURE_TO_DELETE_PLAYER(DeleteOrNotPlayerProcessor.class),

    SKIP(SkipTeamsNameProcessor.class),

    SELECTED_GAME_(GameSelectStateProcessor.class),

    SELECTED_TEAM_(TeamSelectStateProcessor.class),

    SELECTED_MONTH_(SelectDateProcessor.class),

    SELECTED_DATE_(SelectDateProcessor.class),

    EMPTY_DATE(EmptyDateProcessor.class),

    EMPTY_TIME(EmptyTimeProcessor.class),

    EMPTY_CAPACITY(EmptyCapacityProcessor.class),

    EMPTY_TEAM_1_NAME(EmptyTeamAProcessor.class),

    EMPTY_TEAM_2_NAME(EmptyTeamBProcessor.class),

    MAIN_MENU(MainMenuKeyboardProcessor.class),

    REGISTER(RegisterProcessor.class),

    RU(MainMenuKeyboardProcessor.class),

    EN(MainMenuKeyboardProcessor.class),

    SIGN_UP_FOR_GAME(SignUpForGameProcessor.class),

    CREATE_GAME(MainMenuCreateGameProcessor.class),

    DELETE_PLAYER(MainMenuDeletePlayerProcessor.class),

    EMPTY_NAME(EmptyNameProcessor.class),

    EMPTY_PHONE(EmptyPhoneProcessor.class),

    EMPTY_POSITION(EmptyPositionProcessor.class),

    EMPTY_NUMBER(EmptyNumberProcessor.class),

    DELETE(DeletePlayerProcessor.class);

    private final Class<? extends TelegramMessageProcessor> processor;
}
