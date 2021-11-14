package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.yardsportsteamlobby.command.processor.CallbackQueryProcessor;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryService {

    private final CalendarService calendarService;

    private final ConfigurableApplicationContext context;

    private final GameService gameService;

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final PlayerService playerService;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQueryButton) {
        final var chatId = callbackQueryButton.getMessage().getChatId();
        final var userId = callbackQueryButton.getFrom().getId();
        final var callbackData = callbackQueryButton.getData();

        return Optional.ofNullable(CallbackQuerySelect.getProcessorByCallbackData(callbackData))
                .map(processorClazz -> (CallbackQueryProcessor) context.getBean(processorClazz))
                .map(processor -> processor.process(chatId, userId, callbackData))
                .orElse(keyBoardService.createMainMenuKeyboardMessage(chatId));

//        if (Stream.of(PlayerPosition.values()).anyMatch(playerPosition -> playerPosition.name().equals(callbackQueryButton.getData()))) {
//            return playerService.registerPlayer(chatId, userId, callbackQueryButton.getData());
//        } else if (Stream.of(SureOrNotSelect.values()).anyMatch(sureOrNotSelect -> sureOrNotSelect.name().equals(callbackQueryButton.getData()))) {
//            return playerService.deletePlayer(userId, userId, callbackQueryButton.getData());
//        } else if (StringUtils.hasText(callbackQueryButton.getData())
//                && (Stream.of(DateState.values()).anyMatch(dateState -> callbackQueryButton.getData().startsWith(dateState.name()))
//                || Stream.of(SkipSelect.values()).anyMatch(skip -> skip.name().equals(callbackQueryButton.getData())))) {
//            return gameService.createGame(chatId, userId, callbackQueryButton.getData());
//        } else if (Stream.of(Month.values()).anyMatch(month -> month.name().equals(callbackQueryButton.getData()))) {
//            return calendarService.createMonthKeyboard(chatId);
//        } else if (StringUtils.hasText(callbackQueryButton.getData())
//                && callbackQueryButton.getData().startsWith(GameSelectState.SELECTED_GAME_.name())) {
//            return gameService.getTeamRosters(chatId, userId, callbackQueryButton.getData());
//        } else if (StringUtils.hasText(callbackQueryButton.getData())
//                && callbackQueryButton.getData().startsWith(GameSelectState.SELECTED_TEAM_.name())) {
//            return gameService.signUpForGame(chatId, userId, callbackQueryButton.getData());
//        }
//        return keyBoardService.createMainMenuKeyboardMessage(chatId, callbackQueryButton.getMessage().getText());
    }
}
