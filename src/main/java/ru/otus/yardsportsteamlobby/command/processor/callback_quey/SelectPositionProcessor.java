package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.service.PlayerService;

@Component
@RequiredArgsConstructor
public class SelectPositionProcessor implements TelegramMessageProcessor {

    private final PlayerService playerService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return playerService.registerPlayer(chatId, userId, text, userRole);
    }
}
