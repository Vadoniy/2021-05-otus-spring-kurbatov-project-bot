package ru.otus.yardsportsteamlobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.yardsportsteamlobby.command.processor.CallbackQueryProcessor;
import ru.otus.yardsportsteamlobby.command.processor.callback_quey.*;

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

    private final Class<? extends CallbackQueryProcessor> processor;

    public static Class<? extends CallbackQueryProcessor> getProcessorByCallbackData(String callbackData) {
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
