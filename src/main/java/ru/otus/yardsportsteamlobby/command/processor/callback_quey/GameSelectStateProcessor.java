package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.SignUpForGameCommonProcessor;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.SignUpForGameRequestByUserIdService;

@Service
public class GameSelectStateProcessor extends SignUpForGameCommonProcessor {

    public GameSelectStateProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                    SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService, signUpForGameRequestByUserIdService);
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var responseMessage = super.getTeamRosters(chatId, userId, text);
        sendMessage.setReplyMarkup(responseMessage.getReplyMarkup());
        sendMessage.setText(responseMessage.getText());
        botStateService.saveBotStateForUser(userId, BotState.SELECTED_TEAM_);
    }
}
