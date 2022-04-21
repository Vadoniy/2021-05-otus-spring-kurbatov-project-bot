package ru.otus.yardsportsteamlobby.command.processor.create_game;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreateGameRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Service
public class EmptyCapacityProcessor extends AbstractCommonProcessor {

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    public EmptyCapacityProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                  CreateGameRequestByUserIdService createGameRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.createGameRequestByUserIdService = createGameRequestByUserIdService;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text) {
        if (!StringUtils.hasText(text) || !text.matches("\\d{1,2}")) {
            sendMessage.setText(localizationService.getLocalizedMessage("enter.message.players-amount", userId));
        } else {
            final var teamCapacity = Integer.parseInt(text);
            final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                    .map(CreateGameRequestByUserId::getCreateGameRequest)
                    .map(createGameRequest -> createGameRequest.setTeamCapacity(teamCapacity))
                    .orElse(new CreateGameRequest().setTeamCapacity(teamCapacity));
            sendMessage.setText(localizationService.getLocalizedMessage("enter.message.team-a-name", userId));
            sendMessage.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton(userId)));
            createGameRequestByUserIdService.saveCurrentCreateGameRequest(userId, currentCreateGameRequest);
            botStateService.saveBotStateForUser(userId, BotState.EMPTY_TEAM_1_NAME);
        }
    }
}
