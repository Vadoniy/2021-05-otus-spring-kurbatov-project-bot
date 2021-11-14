package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.CallbackQueryProcessor;
import ru.otus.yardsportsteamlobby.service.CalendarService;

@Component
@RequiredArgsConstructor
public class MonthProcessor implements CallbackQueryProcessor {

    private final CalendarService calendarService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text) {
        return calendarService.createMonthKeyboard(chatId);
    }
}
