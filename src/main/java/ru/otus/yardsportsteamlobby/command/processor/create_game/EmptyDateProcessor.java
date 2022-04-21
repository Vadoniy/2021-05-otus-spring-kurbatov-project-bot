package ru.otus.yardsportsteamlobby.command.processor.create_game;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CalendarService;
import ru.otus.yardsportsteamlobby.service.CreateGameRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.time.LocalDate;
import java.time.Month;

@Service
public class EmptyDateProcessor extends AbstractCommonProcessor {

    private final CalendarService calendarService;

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    public EmptyDateProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                              CalendarService calendarService, CreateGameRequestByUserIdService createGameRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.calendarService = calendarService;
        this.createGameRequestByUserIdService = createGameRequestByUserIdService;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text) {
        if (StringUtils.hasText(text) && text.startsWith(CallbackQuerySelect.SELECTED_MONTH_.name())) {
            final var daysOfMonthList = calendarService.fillDaysOfMonth(Month.valueOf(text.replace(CallbackQuerySelect.SELECTED_MONTH_.name(), "")));
            sendMessage.setReplyMarkup(keyBoardService.createKeyboardMarkup(daysOfMonthList));
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.select-date", userId));
        } else {

            //TODO надо разнести логику между SELECTED_MONTH_ и SELECTED_DATE_ в разные процессоры!!!

            final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                    .map(CreateGameRequestByUserId::getCreateGameRequest)
                    .orElse(new CreateGameRequest());
            final var gameDateTime = LocalDate.parse(text.replace(CallbackQuerySelect.SELECTED_DATE_.name(), "")).atStartOfDay();
            currentCreateGameRequest.setGameDateTime(gameDateTime);
            sendMessage.setText(localizationService.getLocalizedMessage("enter.message.game-time", userId));
            createGameRequestByUserIdService.saveCurrentCreateGameRequest(userId, currentCreateGameRequest);
            botStateService.saveBotStateForUser(userId, BotState.EMPTY_TIME);
        }
    }
}
