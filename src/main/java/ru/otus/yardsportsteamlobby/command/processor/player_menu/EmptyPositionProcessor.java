package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerPosition;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

@Component
@RequiredArgsConstructor
public class EmptyPositionProcessor implements PlayerMenuProcessor {

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final PlayerCache playerCache;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    @Override
    public SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        userData.getCreatePlayerRequest().setPosition(PlayerPosition.valueOf(text));
        userData.getCreatePlayerRequest().setUserId(userId);
        final var apiResponse = yardSportsTeamLobbyClient.sendCreatePlayerRequest(userData.getCreatePlayerRequest());
        response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent"));
        if (StringUtils.hasText(apiResponse)) {
            response.setText(apiResponse);
            playerCache.removeData(userId);
        }
        response.setReplyMarkup(keyBoardService.createMainMenuKeyboard());
        return response;
    }
}
