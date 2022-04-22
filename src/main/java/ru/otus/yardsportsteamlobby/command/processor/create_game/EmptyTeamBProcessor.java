package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.Prefix;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreateGameRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.UserRoleService;

@Service
@Slf4j
public class EmptyTeamBProcessor extends AbstractCommonProcessor {

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    private final UserRoleService userRoleService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    public EmptyTeamBProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                               CreateGameRequestByUserIdService createGameRequestByUserIdService, UserRoleService userRoleService,
                               YardSportsTeamLobbyClient yardSportsTeamLobbyClient) {
        super(botStateService, keyBoardService, localizationService);
        this.createGameRequestByUserIdService = createGameRequestByUserIdService;
        this.userRoleService = userRoleService;
        this.yardSportsTeamLobbyClient = yardSportsTeamLobbyClient;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                .map(CreateGameRequestByUserId::getCreateGameRequest)
                .orElse(new CreateGameRequest());

        if (StringUtils.hasText(text) && !Prefix.SKIP.name().equals(text)) {
            currentCreateGameRequest.setTeamNameB(text);
        }
        try {
            yardSportsTeamLobbyClient.sendCreateGameRequest(currentCreateGameRequest, userId);
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent", userId));
        } catch (Exception e) {
            log.info(e.getMessage());
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.smth-is-wrong", userId));
        } finally {
            createGameRequestByUserIdService.removeCurrentCreateGameRequest(userId);
            sendMessage.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRoleService.getUserRoleByUserId(userId)));
            botStateService.saveBotStateForUser(userId, BotState.MAIN_MENU);
        }
    }
}
