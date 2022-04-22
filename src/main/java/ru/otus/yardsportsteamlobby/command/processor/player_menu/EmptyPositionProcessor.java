package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.repository.redis.CreatePlayerRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreatePlayerRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.UserRoleService;

@Service
public class EmptyPositionProcessor extends AbstractCommonProcessor {

    private final CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService;

    private final UserRoleService userRoleService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    public EmptyPositionProcessor(BotStateService botStateService, KeyBoardService keyBoardService,
                                  LocalizationService localizationService, CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService,
                                  UserRoleService userRoleService, YardSportsTeamLobbyClient yardSportsTeamLobbyClient) {
        super(botStateService, keyBoardService, localizationService);
        this.createPlayerRequestByUserIdService = createPlayerRequestByUserIdService;
        this.userRoleService = userRoleService;
        this.yardSportsTeamLobbyClient = yardSportsTeamLobbyClient;
    }

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return super.process(chatId, userId, text, userRole);
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var currentCreatePlayerRequest = createPlayerRequestByUserIdService.getCurrentCreatePlayerRequest(userId)
                .map(CreatePlayerRequestByUserId::getCreatePlayerRequest)
                .map(createPlayerRequest -> createPlayerRequest.setPosition(BotState.valueOf(text)))
                .orElse(new CreatePlayerRequest().setUserId(userId).setPosition(BotState.valueOf(text)));

        try {
            final var savedPlayerRole = yardSportsTeamLobbyClient.sendCreatePlayerRequest(currentCreatePlayerRequest);
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent", userId));
            userRoleService.updateUsersRole(userId, savedPlayerRole);
            sendMessage.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, savedPlayerRole));
        } catch (HttpClientErrorException ex) {
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.smth-is-wrong", userId));
        } finally {
            botStateService.saveBotStateForUser(userId, BotState.MAIN_MENU);
        }
    }
}
