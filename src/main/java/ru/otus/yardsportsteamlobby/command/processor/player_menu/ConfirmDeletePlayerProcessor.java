package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.UserRole;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.DeletePlayerRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.UserRoleService;

import java.util.Optional;

@Service
public class ConfirmDeletePlayerProcessor extends AbstractCommonProcessor {

    private final DeletePlayerRequestByUserIdService deletePlayerRequestByUserIdService;

    private final UserRoleService userRoleService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    public ConfirmDeletePlayerProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                        DeletePlayerRequestByUserIdService deletePlayerRequestByUserIdService,
                                        UserRoleService userRoleService, YardSportsTeamLobbyClient yardSportsTeamLobbyClient) {
        super(botStateService, keyBoardService, localizationService);
        this.deletePlayerRequestByUserIdService = deletePlayerRequestByUserIdService;
        this.userRoleService = userRoleService;
        this.yardSportsTeamLobbyClient = yardSportsTeamLobbyClient;
    }

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return super.process(chatId, userId, text, userRole);
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var newRole = yardSportsTeamLobbyClient.sendDeletePlayerRequest(userId.toString());
        sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent", userId));
        userRoleService.updateUsersRole(userId, Optional.ofNullable(newRole.getBody()).orElse(UserRole.NEW.name()));
        deletePlayerRequestByUserIdService.removeDeletePlayerIdRequest(userId);
        sendMessage.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRoleService.getUserRoleByUserId(userId)));
        botStateService.saveBotStateForUser(userId, BotState.MAIN_MENU);
    }
}
