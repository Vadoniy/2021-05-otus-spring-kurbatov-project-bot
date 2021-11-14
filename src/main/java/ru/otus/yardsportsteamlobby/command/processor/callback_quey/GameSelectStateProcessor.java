package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.CallbackQueryProcessor;
import ru.otus.yardsportsteamlobby.service.GameService;

@Component
@RequiredArgsConstructor
public class GameSelectStateProcessor implements CallbackQueryProcessor {

    private final GameService gameService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text) {
        return gameService.getTeamRosters(chatId, userId, text);
    }
}
