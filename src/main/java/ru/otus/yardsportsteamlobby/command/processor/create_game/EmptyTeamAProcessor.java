package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreateGameRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.util.ArrayList;
import java.util.List;


//FOR ENUM EMPTY_TEAM_1_NAME


@Component
@RequiredArgsConstructor
@Slf4j
public class EmptyTeamAProcessor implements TelegramMessageProcessor {

    private final BotStateService botStateService;

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                .map(CreateGameRequestByUserId::getCreateGameRequest)
                .orElse(new CreateGameRequest());

        if (StringUtils.hasText(text) && !CallbackQuerySelect.SKIP.name().equals(text)) {
            currentCreateGameRequest.setTeamNameA(text);
        }
        response.setText(localizationService.getLocalizedMessage("enter.message.team-b-name", userId));
        response.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton(userId)));
        botStateService.saveBotStateForUser(userId, BotState.EMPTY_TEAM_2_NAME);
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
