package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.DeleteOrNotPlayerProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.GameSelectStateProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.MonthProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.SelectDateProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.SelectPositionProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.SkipTeamsNameProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.TeamSelectStateProcessor;

import java.time.Month;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CallbackQuerySelect {

    FIELD(SelectPositionProcessor.class),

    UNIQUE(SelectPositionProcessor.class),

    SURE_TO_DELETE_PLAYER(DeleteOrNotPlayerProcessor.class),

    NOT_SURE_TO_DELETE_PLAYER(DeleteOrNotPlayerProcessor.class),

    SKIP(SkipTeamsNameProcessor.class),

    SELECTED_GAME_(GameSelectStateProcessor.class),

    SELECTED_TEAM_(TeamSelectStateProcessor.class),

    SELECTED_MONTH_(SelectDateProcessor.class),

    SELECTED_DATE_(SelectDateProcessor.class);

    private final Class<? extends TelegramMessageProcessor> processor;

    public static Class<? extends TelegramMessageProcessor> getProcessorByCallbackData(String callbackData) {
        for (CallbackQuerySelect callbackQuerySelect : values()) {
            if (callbackQuerySelect.name().equals(callbackData) || callbackData.startsWith(callbackQuerySelect.name())) {
                return callbackQuerySelect.processor;
            }
        }
        if (Stream.of(Month.values()).anyMatch(month -> month.name().equals(callbackData))) {
            return MonthProcessor.class;
        }
        return null;
    }
}
