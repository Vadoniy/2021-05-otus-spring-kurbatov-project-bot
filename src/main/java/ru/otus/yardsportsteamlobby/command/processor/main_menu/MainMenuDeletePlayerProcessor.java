package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.service.PlayerService;

@Component
@RequiredArgsConstructor
public class MainMenuDeletePlayerProcessor implements TelegramMessageProcessor {

    private final PlayerService playerService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return playerService.deletePlayer(chatId, userId, text, userRole);
    }
}
