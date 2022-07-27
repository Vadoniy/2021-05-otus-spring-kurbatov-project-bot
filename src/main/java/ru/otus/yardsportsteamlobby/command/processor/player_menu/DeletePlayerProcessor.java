package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.DeletePlayerRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Service
public class DeletePlayerProcessor extends AbstractCommonProcessor {

    private final DeletePlayerRequestByUserIdService deletePlayerRequestByUserIdService;

    public DeletePlayerProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                 DeletePlayerRequestByUserIdService deletePlayerRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.deletePlayerRequestByUserIdService = deletePlayerRequestByUserIdService;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        deletePlayerRequestByUserIdService.saveDeletePlayerIdRequest(userId);
        sendMessage.setReplyMarkup(keyBoardService.createSelectYesNoMarkup(userId));
        sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.sure", userId));
        botStateService.saveBotStateForUser(userId, BotState.DELETE);
    }
}
