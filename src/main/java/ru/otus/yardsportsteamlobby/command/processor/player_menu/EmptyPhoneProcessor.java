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

import java.util.regex.Pattern;

@Service
public class EmptyPhoneProcessor extends AbstractCommonProcessor {

    private final CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService;

    public EmptyPhoneProcessor(BotStateService botStateService, KeyBoardService keyBoardService,
                               LocalizationService localizationService, CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.createPlayerRequestByUserIdService = createPlayerRequestByUserIdService;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        if (Pattern.matches("\\d{10,11}", text)) {
            final var currentCreatePlayerRequest = createPlayerRequestByUserIdService.getCurrentCreatePlayerRequest(userId)
                    .map(CreatePlayerRequestByUserId::getCreatePlayerRequest)
                    .map(createPlayerRequest -> createPlayerRequest.setPhone(text))
                    .orElse(new CreatePlayerRequest().setUserId(userId).setPhone(text));
            createPlayerRequestByUserIdService.saveCurrentCreateGameRequest(userId, currentCreatePlayerRequest);
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.enter-number", userId));
            botStateService.saveBotStateForUser(userId, BotState.EMPTY_NUMBER);
        } else {
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.wrong-phone", userId));
        }
    }
}
