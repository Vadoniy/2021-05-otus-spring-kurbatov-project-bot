package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.CreateGameProcessor;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.cache.CreateGameCache;

import java.util.ArrayList;
import java.util.List;

import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_TEAM_1_NAME;
import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_TEAM_2_NAME;

@Component
@RequiredArgsConstructor
public class EmptyTeamProcessor implements CreateGameProcessor {

    private final CreateGameCache createGameCache;

    private final KeyBoardService keyBoardService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    @Override
    public SendMessage process(GameCreatingStateWithRequest gameData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        if (EMPTY_TEAM_1_NAME == gameData.getCreateGameState()) {
            if (StringUtils.hasText(text) && !CallbackQuerySelect.SKIP.name().equals(text)) {
                gameData.getCreateGameRequest().setTeamNameA(text);
            }
            gameData.setCreateGameState(EMPTY_TEAM_2_NAME);
            response.setText("Введите имя команды Б или нажмите пропустить (будет выбрано имя по умолчанию)");
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton()));
        } else {
            if (StringUtils.hasText(text) && !CallbackQuerySelect.SKIP.name().equals(text)) {
                gameData.getCreateGameRequest().setTeamNameB(text);
            }
            final var apiResponse = yardSportsTeamLobbyClient.sendCreateGameRequest(gameData.getCreateGameRequest());
            response.setText("Запрос на создание отправлен");
            if (StringUtils.hasText(apiResponse)) {
                createGameCache.removeData(userId);
            }
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard());
        }
        return response;
    }

    private ArrayList<List<InlineKeyboardButton>> createSkipButton() {
        final var skipButton = new InlineKeyboardButton();
        skipButton.setText("Пропустить");
        skipButton.setCallbackData(CallbackQuerySelect.SKIP.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(skipButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        return keyBoardList;
    }
}
