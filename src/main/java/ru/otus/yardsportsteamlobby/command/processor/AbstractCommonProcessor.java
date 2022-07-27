package ru.otus.yardsportsteamlobby.command.processor;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.util.ArrayList;
import java.util.List;

import static ru.otus.yardsportsteamlobby.enums.BotState.SKIP;

@RequiredArgsConstructor
public abstract class AbstractCommonProcessor implements TelegramMessageProcessor {

    protected final BotStateService botStateService;

    protected final KeyBoardService keyBoardService;

    protected final LocalizationService localizationService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        fillTheResponse(response, chatId, userId, text, userRole);
        return response;
    }

    protected ArrayList<List<InlineKeyboardButton>> createSkipButton(Long userId) {
        final var skipButton = new InlineKeyboardButton();
        skipButton.setText(localizationService.getLocalizedMessage("select.skip", userId));
        skipButton.setCallbackData(SKIP.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(skipButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        return keyBoardList;
    }

    protected abstract void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole);
}
