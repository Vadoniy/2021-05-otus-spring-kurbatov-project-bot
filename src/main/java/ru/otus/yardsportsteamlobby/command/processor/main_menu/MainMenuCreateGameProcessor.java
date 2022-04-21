package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.service.GameService;

@Component
@RequiredArgsConstructor
public class MainMenuCreateGameProcessor implements TelegramMessageProcessor {

    private final GameService gameService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return gameService.createGame(chatId, userId, text, userRole);
    }
}
