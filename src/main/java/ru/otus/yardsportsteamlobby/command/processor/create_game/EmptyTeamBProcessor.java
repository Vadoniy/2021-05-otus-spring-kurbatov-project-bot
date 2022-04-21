package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreateGameRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;








//FOR ENUM EMPTY_TEAM_2_NAME










@Component
@RequiredArgsConstructor
@Slf4j
public class EmptyTeamBProcessor implements TelegramMessageProcessor {

    private final BotStateService botStateService;

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                .map(CreateGameRequestByUserId::getCreateGameRequest)
                .orElse(new CreateGameRequest());

        if (StringUtils.hasText(text) && !CallbackQuerySelect.SKIP.name().equals(text)) {
            currentCreateGameRequest.setTeamNameB(text);
        }
        try {
            yardSportsTeamLobbyClient.sendCreateGameRequest(currentCreateGameRequest, userId);
            response.setText(localizationService.getLocalizedMessage("one-way.message.request-is-sent", userId));
        } catch (Exception e) {
            log.info(e.getMessage());
            response.setText(localizationService.getLocalizedMessage("one-way.message.smth-is-wrong", userId));
        } finally {
            createGameRequestByUserIdService.removeCurrentCreateGameRequest(userId);
            botStateService.saveBotStateForUser(userId, BotState.MAIN_MENU);
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRole));
        }

        return response;
    }
}
