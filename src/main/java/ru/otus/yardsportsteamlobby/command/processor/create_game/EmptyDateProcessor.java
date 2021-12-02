package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.CreateGameProcessor;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.service.CalendarService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.time.LocalDate;
import java.time.Month;

import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_TIME;

@Component
@RequiredArgsConstructor
public class EmptyDateProcessor implements CreateGameProcessor {

    private final CalendarService calendarService;

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    @Override
    public SendMessage process(GameCreatingStateWithRequest gameData, Long chatId, String text, Long userId, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (StringUtils.hasText(text) && text.startsWith(CallbackQuerySelect.SELECTED_MONTH_.name())) {
            final var daysOfMonthList = calendarService.fillDaysOfMonth(Month.valueOf(text.replace(CallbackQuerySelect.SELECTED_MONTH_.name(), "")));
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(daysOfMonthList));
            response.setText(localizationService.getLocalizedMessage("one-way.message.select-date", userId));
            return response;
        }
        final var request = gameData.getCreateGameRequest();
        final var gameDateTime = LocalDate.parse(text.replace(CallbackQuerySelect.SELECTED_DATE_.name(), "")).atStartOfDay();
        request.setGameDateTime(gameDateTime);
        gameData.setCreateGameState(EMPTY_TIME);
        response.setText(localizationService.getLocalizedMessage("enter.message.game-time", userId));
        return response;
    }
}
