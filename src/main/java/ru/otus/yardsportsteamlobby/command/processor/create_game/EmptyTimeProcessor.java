package ru.otus.yardsportsteamlobby.command.processor.create_game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.CreateGameProcessor;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_CAPACITY;

@Component
@RequiredArgsConstructor
public class EmptyTimeProcessor implements CreateGameProcessor {

    @Override
    public SendMessage process(GameCreatingStateWithRequest gameData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (!isTimeInputOk(text)) {
            response.setText("Неправильный формат времени, укажите время игры в формате HH:mm (например, 09:15 или 21:15).");
        } else {
            final var gameTime = LocalTime.parse(text);
            final var gameDate = gameData.getCreateGameRequest().getGameDateTime().toLocalDate();
            final var gameDateTime = LocalDateTime.of(gameDate, gameTime);
            gameData.getCreateGameRequest().setGameDateTime(gameDateTime);
            gameData.setCreateGameState(EMPTY_CAPACITY);
            response.setText("Введите количество игроков в команде (например, 6)");
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
