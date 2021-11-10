package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.enums.DateState;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final KeyBoardService keyBoardService;

    public SendMessage createMonthKeyboard(Long chatId) {
        final var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(12);
        for (Month month : Month.values()) {
            final var monthButton = new InlineKeyboardButton();
            monthButton.setText(month.name());
            monthButton.setCallbackData(DateState.SELECTED_MONTH_.name() + month.name());
            keyBoardList.add(List.of(monthButton));
        }
        sendMessage.setReplyMarkup(keyBoardService.createKeyboardMarkup(keyBoardList));
        sendMessage.setText("Выберите месяц");
        return sendMessage;
    }

    public List<List<InlineKeyboardButton>> fillDaysOfMonth(Month month) {
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(12);
        final var row0 = new ArrayList<InlineKeyboardButton>();
        final var currentMonthButton = new InlineKeyboardButton();
        final var currentDate = LocalDate.now();
        final var currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();
        if (currentMonth.getValue() > month.getValue()) {
            currentYear = currentDate.getYear() + 1;
        }
        currentMonthButton.setText(month.name());
        currentMonthButton.setCallbackData(month.name());
        row0.add(currentMonthButton);
        keyBoardList.add(row0);
        for (int i = 1; i < month.length(Year.isLeap(currentYear)); ) {
            final var anotherRow = new ArrayList<InlineKeyboardButton>(6);
            for (int j = 0; j < 6; j++, i++) {
                final var anotherDay = new InlineKeyboardButton();
                anotherDay.setText(String.valueOf(i));
                anotherDay.setCallbackData(DateState.SELECTED_DATE_.name()
                        + LocalDate.of(currentYear, month, i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                anotherRow.add(anotherDay);
            }
            keyBoardList.add(anotherRow);
        }
        return keyBoardList;
    }
}
