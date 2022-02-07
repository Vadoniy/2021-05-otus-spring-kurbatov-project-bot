package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.command.processor.CreateGameProcessor;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.util.ArrayList;
import java.util.List;

import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_TEAM_1_NAME;

@Component
@RequiredArgsConstructor
public class EmptyCapacityProcessor implements CreateGameProcessor {

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    @Override
    public SendMessage process(GameCreatingStateWithRequest gameData, Long chatId, String text, Long userId, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (!StringUtils.hasText(text) || !text.matches("\\d{1,2}")) {
            response.setText(localizationService.getLocalizedMessage("enter.message.players-amount", userId));
        } else {
            final var teamCapacity = Integer.parseInt(text);
            gameData.getCreateGameRequest().setTeamCapacity(teamCapacity);
            gameData.setCreateGameState(EMPTY_TEAM_1_NAME);
            response.setText(localizationService.getLocalizedMessage("enter.message.team-a-name", userId));
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton(userId)));
        }
        return response;
    }

    private ArrayList<List<InlineKeyboardButton>> createSkipButton(Long userId) {
        final var skipButton = new InlineKeyboardButton();
        skipButton.setText(localizationService.getLocalizedMessage("select.skip", userId));
        skipButton.setCallbackData(CallbackQuerySelect.SKIP.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(skipButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        return keyBoardList;
    }
}
