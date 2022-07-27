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

import static ru.otus.yardsportsteamlobby.enums.BotState.SKIP;

@Service
public class EmptyTeamAProcessor extends AbstractCommonProcessor {

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    public EmptyTeamAProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                               CreateGameRequestByUserIdService createGameRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.createGameRequestByUserIdService = createGameRequestByUserIdService;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                .map(CreateGameRequestByUserId::getCreateGameRequest)
                .orElse(new CreateGameRequest());

        if (StringUtils.hasText(text) && !SKIP.name().equals(text)) {
            currentCreateGameRequest.setTeamNameA(text);
        }
        sendMessage.setText(localizationService.getLocalizedMessage("enter.message.team-b-name", userId));
        sendMessage.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton(userId)));
        botStateService.saveBotStateForUser(userId, BotState.EMPTY_TEAM_2_NAME);
    }
}
