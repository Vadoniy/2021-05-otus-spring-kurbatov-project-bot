package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.enums.MainMenuSelect;

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

    public InlineKeyboardMarkup createSelectPositionMarkup() {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final var fieldButton = new InlineKeyboardButton();
        fieldButton.setText(localizationService.getLocalizedMessage("select.field"));
        fieldButton.setCallbackData(CallbackQuerySelect.FIELD.name());
        final var uniquePositionButton = new InlineKeyboardButton();
        uniquePositionButton.setText(localizationService.getLocalizedMessage("select.unique"));
        uniquePositionButton.setCallbackData(CallbackQuerySelect.UNIQUE.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(fieldButton);
        keyboardButtonsRow1.add(uniquePositionButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(keyBoardList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createSelectYesNoMarkup() {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final var yesButton = new InlineKeyboardButton();
        yesButton.setText(localizationService.getLocalizedMessage("select.yes"));
        yesButton.setCallbackData(CallbackQuerySelect.SURE_TO_DELETE_PLAYER.name());
        final var noButton = new InlineKeyboardButton();
        noButton.setText(localizationService.getLocalizedMessage("select.no"));
        noButton.setCallbackData(CallbackQuerySelect.NOT_SURE_TO_DELETE_PLAYER.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(yesButton);
        keyboardButtonsRow1.add(noButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(keyBoardList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup createMainMenuKeyboard() {
        final var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        final var keyboard = new ArrayList<KeyboardRow>();
        final var row1 = new KeyboardRow();
        final var row2 = new KeyboardRow();
        final var row3 = new KeyboardRow();
        final var row4 = new KeyboardRow();
        row1.add(new KeyboardButton(localizationService.getLocalizedMessage(MainMenuSelect.REGISTER.getMessage())));
        row2.add(new KeyboardButton(localizationService.getLocalizedMessage(MainMenuSelect.SIGN_UP_FOR_GAME.getMessage())));
        row3.add(new KeyboardButton(localizationService.getLocalizedMessage(MainMenuSelect.CREATE_GAME.getMessage())));
        row4.add(new KeyboardButton(localizationService.getLocalizedMessage(MainMenuSelect.DELETE_PLAYER.getMessage())));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public SendMessage createMainMenuKeyboardMessage(long chatId) {
        final var textMessage = localizationService.getLocalizedMessage("main.menu.greetings");
        return createKeyboardMessage(chatId, textMessage, createMainMenuKeyboard());
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
}
