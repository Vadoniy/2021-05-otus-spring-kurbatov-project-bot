package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.UserRoleService;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmptyPositionProcessor implements PlayerMenuProcessor {

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final PlayerCache playerCache;

    private final UserRoleService userRoleService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    @Override
    public SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        userData.getCreatePlayerRequest().setPosition(CallbackQuerySelect.valueOf(text));
        userData.getCreatePlayerRequest().setUserId(userId);
        try {
            final var savedPlayerRole = yardSportsTeamLobbyClient.sendCreatePlayerRequest(userData.getCreatePlayerRequest());
            response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent"));
            userRoleService.updateUsersRole(userId, savedPlayerRole);
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard(savedPlayerRole));
        } catch (HttpClientErrorException ex) {
            response.setText(localizationService.getLocalizedMessage("one-way.message.smth-is-wrong"));
        } finally {
            playerCache.removeData(userId);
        }
        return response;
    }
}
