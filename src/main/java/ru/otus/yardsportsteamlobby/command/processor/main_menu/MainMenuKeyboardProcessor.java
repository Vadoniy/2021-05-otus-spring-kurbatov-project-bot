package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Service
public class MainMenuKeyboardProcessor extends AbstractCommonProcessor {

    public MainMenuKeyboardProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService) {
        super(botStateService, keyBoardService, localizationService);
    }

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return keyBoardService.createMainMenuKeyboardMessage(userId, userRole);
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
    }
}