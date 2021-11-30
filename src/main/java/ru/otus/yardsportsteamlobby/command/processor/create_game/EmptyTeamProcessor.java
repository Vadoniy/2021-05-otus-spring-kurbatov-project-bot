package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.CreateGameProcessor;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.cache.CreateGameCache;

import java.util.ArrayList;
import java.util.List;

import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_TEAM_1_NAME;
import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_TEAM_2_NAME;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmptyTeamProcessor implements CreateGameProcessor {

    private final CreateGameCache createGameCache;

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    @Override
    public SendMessage process(GameCreatingStateWithRequest gameData, Long chatId, String text, Long userId, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        if (EMPTY_TEAM_1_NAME == gameData.getCreateGameState()) {
            if (StringUtils.hasText(text) && !CallbackQuerySelect.SKIP.name().equals(text)) {
                gameData.getCreateGameRequest().setTeamNameA(text);
            }
            gameData.setCreateGameState(EMPTY_TEAM_2_NAME);
            response.setText(localizationService.getLocalizedMessage("enter.message.team-b-name"));
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton()));
        } else {
            if (StringUtils.hasText(text) && !CallbackQuerySelect.SKIP.name().equals(text)) {
                gameData.getCreateGameRequest().setTeamNameB(text);
            }
            try {
                yardSportsTeamLobbyClient.sendCreateGameRequest(gameData.getCreateGameRequest(), userId);
                response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent"));
            } catch (Exception e) {
                log.info(e.getMessage());
                response.setText(localizationService.getLocalizedMessage("one-way.message.smth-is-wrong"));
            } finally {
                createGameCache.removeData(userId);
                response.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userRole));
            }
        }
        return response;
    }

    private ArrayList<List<InlineKeyboardButton>> createSkipButton() {
        final var skipButton = new InlineKeyboardButton();
        skipButton.setText(localizationService.getLocalizedMessage("select.skip"));
        skipButton.setCallbackData(CallbackQuerySelect.SKIP.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(skipButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        return keyBoardList;
    }
}
