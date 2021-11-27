package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.enums.UserRole;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.UserRoleService;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeletePlayerProcessor implements PlayerMenuProcessor {

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final PlayerCache playerCache;

    private final UserRoleService userRoleService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    @Override
    public SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (PlayerRegistrationState.DELETE == userData.getPlayerRegistrationState()) {
            if (CallbackQuerySelect.SURE_TO_DELETE_PLAYER == CallbackQuerySelect.valueOf(text)) {
                final var newRole = yardSportsTeamLobbyClient.sendDeletePlayerRequest(userId.toString());
                response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent"));
                userRoleService.updateUsersRole(userId, Optional.ofNullable(newRole.getBody()).orElse(UserRole.NEW.name()));
            } else {
                response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-deleted"));
            }
            playerCache.removeData(userId);
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userRoleService.getUserRoleByUserId(userId)));
        } else {
            final var registrationStateWithRequest = new RegistrationStateWithRequest()
                    .setPlayerRegistrationState(PlayerRegistrationState.DELETE);
            playerCache.addData(userId, registrationStateWithRequest);
            response.setReplyMarkup(keyBoardService.createSelectYesNoMarkup());
            response.setText(localizationService.getLocalizedMessage("one-way.message.sure"));
        }
        return response;
    }
}
