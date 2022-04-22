package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.SignUpForGameCommonProcessor;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.SignUpForGameRequestByUserIdService;

@Service
public class GameSelectStateProcessor extends SignUpForGameCommonProcessor {

    private final SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService;

    public GameSelectStateProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                    SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService, SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService1) {
        super(botStateService, keyBoardService, localizationService, signUpForGameRequestByUserIdService);
        this.signUpForGameRequestByUserIdService = signUpForGameRequestByUserIdService1;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        super.getTeamRosters(chatId, userId, text);
    }
}
