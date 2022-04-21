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
public class EmptyNameProcessor extends AbstractCommonProcessor {

    private final CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService;

    public EmptyNameProcessor(BotStateService botStateService, KeyBoardService keyBoardService,
                              LocalizationService localizationService, CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.createPlayerRequestByUserIdService = createPlayerRequestByUserIdService;
    }

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return super.process(chatId, userId, text, userRole);
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text) {
        sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.enter-your-phone", userId));
        final var currentCreatePlayerRequest = createPlayerRequestByUserIdService.getCurrentCreatePlayerRequest(userId)
                .map(CreatePlayerRequestByUserId::getCreatePlayerRequest)
                .map(createPlayerRequest -> createPlayerRequest.setName(text))
                .orElse(new CreatePlayerRequest().setUserId(userId).setName(text));
        createPlayerRequestByUserIdService.saveCurrentCreateGameRequest(userId, currentCreatePlayerRequest);
        botStateService.saveBotStateForUser(userId, BotState.EMPTY_PHONE);
    }
}
