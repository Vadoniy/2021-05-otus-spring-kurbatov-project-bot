package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.yardsportsteamlobby.enums.*;
import ru.otus.yardsportsteamlobby.service.cache.CreateGameCache;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import javax.validation.constraints.NotNull;
import java.time.Month;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryService {

    private final CalendarService calendarService;

    private final GameService gameService;

    private final KeyBoardService keyBoardService;

    private final PlayerService playerService;

    private final CreateGameCache createGameCache;

    private final PlayerCache playerCache;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQueryButton) {
        final var chatId = callbackQueryButton.getMessage().getChatId();
        final var userId = callbackQueryButton.getFrom().getId();
        if (Stream.of(PlayerPosition.values()).anyMatch(playerPosition -> playerPosition.name().equals(callbackQueryButton.getData()))) {
            return playerService.registerPlayer(chatId, userId, callbackQueryButton.getData());
        } else if (Stream.of(YesNoSelect.values()).anyMatch(yesNoSelect -> yesNoSelect.name().equals(callbackQueryButton.getData()))) {
            return playerService.deletePlayer(userId, userId, YesNoSelect.valueOf(callbackQueryButton.getData()));
        } else if (StringUtils.hasText(callbackQueryButton.getData())
                && (Stream.of(DateState.values()).anyMatch(dateState -> callbackQueryButton.getData().startsWith(dateState.name()))
                || Stream.of(SkipSelect.values()).anyMatch(skip -> skip.name().equals(callbackQueryButton.getData())))) {
            return gameService.createGame(chatId, userId, callbackQueryButton.getData());
        } else if (Stream.of(Month.values()).anyMatch(month -> month.name().equals(callbackQueryButton.getData()))) {
            return calendarService.createMonthKeyboard(chatId);
        } else if (StringUtils.hasText(callbackQueryButton.getData())
                && callbackQueryButton.getData().startsWith(GameSelectState.SELECTED_GAME_.name())) {
            return gameService.getTeamRosters(chatId, userId, callbackQueryButton.getData());
        } else if (StringUtils.hasText(callbackQueryButton.getData())
                && callbackQueryButton.getData().startsWith(GameSelectState.SELECTED_TEAM_.name())) {
            return gameService.signUpForGame(chatId, userId, callbackQueryButton.getData());
        }
        return getKeyboardMainMenu(chatId, callbackQueryButton.getMessage().getText());
    }

    public SendMessage processInputMessage(@NotNull Message message) {
        final var chatId = message.getChatId();
        final var userId = message.getFrom().getId();
        final var text = message.getText();
        if ("/start".equals(text)) {
            return getKeyboardMainMenu(chatId, text);
        } else if ("Зарегистрироваться".equals(text)) {
            return playerService.registerPlayer(chatId, userId, text);
        } else if ("Удалить свои данные".equals(text)) {
            return playerService.deletePlayer(userId, userId, null);
        } else if ("Создать игру".equals(text)) {
            return gameService.createGame(chatId, userId, text);
        } else if ("Записаться на игру".equals(text)) {
            return gameService.getGameList(chatId, userId);
        } else if (isDayOfGame(text)) {
            return gameService.createGame(chatId, userId, text);
        } else if (playerCache.isDataAlreadyExists(userId)) {
            return playerService.registerPlayer(chatId, userId, message.getText());
        } else if (createGameCache.isDataAlreadyExists(userId)) {
            return gameService.createGame(chatId, userId, message.getText());
        }
        return getKeyboardMainMenu(chatId, text);
    }

    private SendMessage getKeyboardMainMenu(long chatId, String textMessage) {
        final var replyKeyboardMarkup = keyBoardService.createMainMenuKeyboard();
        return keyBoardService.createKeyboardMessage(chatId, textMessage, replyKeyboardMarkup);
    }

    private boolean isDayOfGame(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        } else {
            return Stream.of(DateState.values()).anyMatch(dateState -> text.startsWith(dateState.name() + "_"));
        }
    }
}
