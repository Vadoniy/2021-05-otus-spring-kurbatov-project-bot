package ru.otus.yardsportsteamlobby.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.enums.MainMenuSelect;
import ru.otus.yardsportsteamlobby.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyBoardService {

    private final LocalizationService localizationService;

    public InlineKeyboardMarkup createKeyboardMarkup(List<List<InlineKeyboardButton>> inlineKeyboardButtons) {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createSelectPositionMarkup(Long userId) {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final var fieldButton = new InlineKeyboardButton();
        fieldButton.setText(localizationService.getLocalizedMessage("select.field", userId));
        fieldButton.setCallbackData(CallbackQuerySelect.FIELD.name());
        final var uniquePositionButton = new InlineKeyboardButton();
        uniquePositionButton.setText(localizationService.getLocalizedMessage("select.unique", userId));
        uniquePositionButton.setCallbackData(CallbackQuerySelect.UNIQUE.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(fieldButton);
        keyboardButtonsRow1.add(uniquePositionButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(keyBoardList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createSelectYesNoMarkup(Long userId) {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final var yesButton = new InlineKeyboardButton();
        yesButton.setText(localizationService.getLocalizedMessage("select.yes", userId));
        yesButton.setCallbackData(BotState.SURE_TO_DELETE_PLAYER.name());
        final var noButton = new InlineKeyboardButton();
        noButton.setText(localizationService.getLocalizedMessage("select.no", userId));
        noButton.setCallbackData(BotState.NOT_SURE_TO_DELETE_PLAYER.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(yesButton);
        keyboardButtonsRow1.add(noButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(keyBoardList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup createMainMenuKeyboard(Long userId, String userRole) {
        final var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        final var keyboard = new ArrayList<KeyboardRow>();
        final var row1 = new KeyboardRow();
        row1.add(new KeyboardButton(resolveButtonText(MainMenuSelect.REGISTER, userId)));
        keyboard.add(row1);
        if (UserRole.USER.name().equals(userRole) || UserRole.ADMIN.name().equals(userRole)) {
            final var row2 = new KeyboardRow();
            row2.add(new KeyboardButton(resolveButtonText(MainMenuSelect.SIGN_UP_FOR_GAME, userId)));
            keyboard.add(row2);
        }
        if (UserRole.USER.name().equals(userRole)) {
            final var row3 = new KeyboardRow();
            row3.add(new KeyboardButton(resolveButtonText(MainMenuSelect.DELETE_PLAYER, userId)));
            keyboard.add(row3);
        }
        if (UserRole.ADMIN.name().equals(userRole)) {
            final var row4 = new KeyboardRow();
            row4.add(new KeyboardButton(resolveButtonText(MainMenuSelect.CREATE_GAME, userId)));
            keyboard.add(row4);
        }
        final var row5 = new KeyboardRow();
        row5.add(new KeyboardButton(resolveButtonText(MainMenuSelect.RU, userId)));
        row5.add(new KeyboardButton(resolveButtonText(MainMenuSelect.EN, userId)));
        keyboard.add(row5);
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public SendMessage createMainMenuKeyboardMessage(long userId, String userRole) {
        final var textMessage = localizationService.getLocalizedMessage("main.menu.greetings", userId);
        return createKeyboardMessage(userId, textMessage, createMainMenuKeyboard(userId, userRole));
    }

    public SendMessage createKeyboardMessage(long chatId, String textMessage, ReplyKeyboard keyboardMarkup) {
        final var sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);
        if (keyboardMarkup != null) {
            sendMessage.setReplyMarkup(keyboardMarkup);
        }
        return sendMessage;
    }

    private String resolveButtonText(MainMenuSelect mainMenuSelect, Long userId) {
        final var emoji = EmojiParser.parseToUnicode(mainMenuSelect.getEmoji());
        return localizationService.getLocalizedMessage(mainMenuSelect.getMessage(), userId) + emoji;
    }
}
