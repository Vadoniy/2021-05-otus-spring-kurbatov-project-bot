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
import ru.otus.yardsportsteamlobby.enums.PlayerPosition;
import ru.otus.yardsportsteamlobby.enums.YesNoSelect;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyBoardService {

    public InlineKeyboardMarkup createKeyboardMarkup(List<List<InlineKeyboardButton>> inlineKeyboardButtons) {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createSelectPositionMarkup() {
        final var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final var fieldButton = new InlineKeyboardButton();
        fieldButton.setText("Полевой игрок");
        fieldButton.setCallbackData(PlayerPosition.FIELD.name());
        final var uniquePositionButton = new InlineKeyboardButton();
        uniquePositionButton.setText("Вратарь");
        uniquePositionButton.setCallbackData(PlayerPosition.UNIQUE.name());
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
        yesButton.setText("Да");
        yesButton.setCallbackData(YesNoSelect.YES.name());
        final var noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(YesNoSelect.NO.name());
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
        row1.add(new KeyboardButton("Зарегистрироваться"));
        row2.add(new KeyboardButton("Записаться на игру"));
        row3.add(new KeyboardButton("Создать игру"));
        row4.add(new KeyboardButton("Удалить свои данные"));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
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
