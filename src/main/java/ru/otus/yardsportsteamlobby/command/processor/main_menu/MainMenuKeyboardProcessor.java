package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.MainMenuProcessor;
import ru.otus.yardsportsteamlobby.enums.MainMenuSelect;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.cache.LanguageCache;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
public class MainMenuKeyboardProcessor implements MainMenuProcessor {

    private final KeyBoardService keyBoardService;

    private final LanguageCache languageCache;

    @Override
    public SendMessage process(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text, String userRole) {
        EmojiParser.extractEmojis(text).stream()
                .filter(text::contains)
                .findAny()
                .map(EmojiParser::parseToAliases)
                .map(MainMenuSelect::resolveByEmoji)
                .ifPresent(mainMenuSelect -> languageCache.addData(userId, mainMenuSelect.name()));
        return keyBoardService.createMainMenuKeyboardMessage(userId, userRole);
    }
}