package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Service
public class DeleteOrNotPlayerProcessor extends AbstractCommonProcessor {

    public DeleteOrNotPlayerProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService) {
        super(botStateService, keyBoardService, localizationService);
    }

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return super.process(chatId, userId, text, userRole);
    }

    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.sure", userId));
        sendMessage.setReplyMarkup(keyBoardService.createSelectYesNoMarkup(userId));
        botStateService.saveBotStateForUser(userId, BotState.DELETE);
    }
}
