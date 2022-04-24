package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.repository.redis.CreatePlayerRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreatePlayerRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Service
public class EmptyNumberProcessor extends AbstractCommonProcessor {

    private final CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService;

    private final KeyBoardService keyBoardService;

    public EmptyNumberProcessor(BotStateService botStateService, KeyBoardService keyBoardService,
                                LocalizationService localizationService, CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService,
                                KeyBoardService keyBoardService1) {
        super(botStateService, keyBoardService, localizationService);
        this.createPlayerRequestByUserIdService = createPlayerRequestByUserIdService;
        this.keyBoardService = keyBoardService1;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        try {
            final var currentCreatePlayerRequest = createPlayerRequestByUserIdService.getCurrentCreatePlayerRequest(userId)
                    .map(CreatePlayerRequestByUserId::getCreatePlayerRequest)
                    .map(createPlayerRequest -> createPlayerRequest.setNumber(Integer.parseInt(text)))
                    .orElse(new CreatePlayerRequest().setUserId(userId).setNumber(Integer.parseInt(text)));
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.select-position", userId));
            sendMessage.setReplyMarkup(keyBoardService.createSelectPositionMarkup(userId));
            createPlayerRequestByUserIdService.saveCurrentCreateGameRequest(userId, currentCreatePlayerRequest);
        } catch (NumberFormatException nfe) {
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.wrong-number", userId));
        }
        botStateService.saveBotStateForUser(userId, BotState.EMPTY_POSITION);
    }
}
