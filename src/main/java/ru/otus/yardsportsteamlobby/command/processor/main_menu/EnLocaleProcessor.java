package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.MainMenuButtonName;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.cache.LanguageCache;

@Service
public class EnLocaleProcessor extends MainMenuKeyboardProcessor {

    private final LanguageCache languageCache;

    public EnLocaleProcessor(BotStateService botStateService, KeyBoardService keyBoardService,
                             LocalizationService localizationService, LanguageCache languageCache) {
        super(botStateService, keyBoardService, localizationService);
        this.languageCache = languageCache;
    }

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        languageCache.addData(userId, MainMenuButtonName.en.toString());
        botStateService.saveBotStateForUser(userId, BotState.MAIN_MENU);
        return super.process(chatId, userId, text, userRole);
    }
}