package ru.otus.yardsportsteamlobby.command.processor.create_game;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.Prefix;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreateGameRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.time.LocalDate;

@Service
public class EmptyDateProcessor extends AbstractCommonProcessor {

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    public EmptyDateProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                              CreateGameRequestByUserIdService createGameRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.createGameRequestByUserIdService = createGameRequestByUserIdService;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                .map(CreateGameRequestByUserId::getCreateGameRequest)
                .orElse(new CreateGameRequest());
        final var gameDateTime = LocalDate.parse(text.replace(Prefix.SELECTED_DATE_.name(), "")).atStartOfDay();
        currentCreateGameRequest.setGameDateTime(gameDateTime);
        sendMessage.setText(localizationService.getLocalizedMessage("enter.message.game-time", userId));
        createGameRequestByUserIdService.saveCurrentCreateGameRequest(userId, currentCreateGameRequest);
        botStateService.saveBotStateForUser(userId, BotState.EMPTY_TIME);
    }
}
