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

import java.util.ArrayList;
import java.util.List;

import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_TEAM_1_NAME;

@Component
@RequiredArgsConstructor
public class EmptyCapacityProcessor implements CreateGameProcessor {

    private final KeyBoardService keyBoardService;

    @Override
    public SendMessage process(GameCreatingStateWithRequest gameData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (!StringUtils.hasText(text) || !text.matches("\\d{1,2}")) {
            response.setText("Введите количество игроков в команде (например, 6)");
        } else {
            final var teamCapacity = Integer.parseInt(text);
            gameData.getCreateGameRequest().setTeamCapacity(teamCapacity);
            gameData.setCreateGameState(EMPTY_TEAM_1_NAME);
            response.setText("Введите имя команды А или нажмите пропустить (будет выбрано имя по умолчанию)");
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton()));
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
