package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.service.CalendarService;

@Component
@RequiredArgsConstructor
public class MonthProcessor implements TelegramMessageProcessor {

    private final CalendarService calendarService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return calendarService.createMonthKeyboard(chatId, userId);
    }
}
