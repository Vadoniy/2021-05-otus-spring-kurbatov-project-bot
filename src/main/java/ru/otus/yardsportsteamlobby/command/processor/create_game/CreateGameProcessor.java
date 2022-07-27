package ru.otus.yardsportsteamlobby.command.processor.create_game;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CalendarService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Service
public class CreateGameProcessor extends AbstractCommonProcessor {

    private final CalendarService calendarService;

    public CreateGameProcessor(BotStateService botStateService, KeyBoardService keyBoardService,
                               LocalizationService localizationService, CalendarService calendarService) {
        super(botStateService, keyBoardService, localizationService);
        this.calendarService = calendarService;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var monthListKeyboard = calendarService.createMonthKeyboard(chatId, userId);
        sendMessage.setReplyMarkup(monthListKeyboard.getReplyMarkup());
        sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.select-month", userId));
        botStateService.saveBotStateForUser(userId, BotState.EMPTY_MONTH);
    }
}
