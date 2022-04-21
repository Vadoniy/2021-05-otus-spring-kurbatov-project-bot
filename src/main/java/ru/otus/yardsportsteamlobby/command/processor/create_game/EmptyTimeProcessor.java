package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.CreateGameRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class EmptyTimeProcessor implements TelegramMessageProcessor {

    private final BotStateService botStateService;

    private final CreateGameRequestByUserIdService createGameRequestByUserIdService;

    private final LocalizationService localizationService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (!isTimeInputOk(text)) {
            response.setText(localizationService.getLocalizedMessage("enter.message.wrong-time", userId));
        } else {
            final var gameTime = LocalTime.parse(text);
            final var currentCreateGameRequest = createGameRequestByUserIdService.getCurrentCreateGameRequest(userId)
                    .map(CreateGameRequestByUserId::getCreateGameRequest)
                    .orElse(new CreateGameRequest());
            final var gameDate = currentCreateGameRequest.getGameDateTime().toLocalDate();
            final var gameDateTime = LocalDateTime.of(gameDate, gameTime);
            currentCreateGameRequest.setGameDateTime(gameDateTime);
            response.setText(localizationService.getLocalizedMessage("enter.message.players-amount", userId));
            createGameRequestByUserIdService.saveCurrentCreateGameRequest(userId, currentCreateGameRequest);
            botStateService.saveBotStateForUser(userId, BotState.EMPTY_CAPACITY);
        }
        return response;
    }

    private boolean isTimeInputOk(String inputTime) {
        if (!StringUtils.hasText(inputTime) || !inputTime.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        final var timeUnits = inputTime.split(":");
        final var hour = Integer.parseInt(timeUnits[0]);
        final var minute = Integer.parseInt(timeUnits[1]);
        return hour < 24 && hour > 0 && minute < 60 && minute > 0;
    }
}
