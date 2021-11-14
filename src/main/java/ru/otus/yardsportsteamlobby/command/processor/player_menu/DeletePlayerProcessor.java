package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.enums.YesNoSelect;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

@Component
@RequiredArgsConstructor
public class DeletePlayerProcessor implements PlayerMenuProcessor {

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final PlayerCache playerCache;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    @Override
    public SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (PlayerRegistrationState.DELETE == userData.getPlayerRegistrationState()) {
            if (YesNoSelect.YES == YesNoSelect.valueOf(text)) {
                yardSportsTeamLobbyClient.sendDeletePlayerRequest(userId.toString());
                response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent"));
            } else {
                response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-deleted"));
            }
            playerCache.removeData(userId);
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
