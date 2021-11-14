package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.CallbackQueryProcessor;
import ru.otus.yardsportsteamlobby.service.PlayerService;

@Component
@RequiredArgsConstructor
public class DeleteOrNotPlayerProcessor implements CallbackQueryProcessor {

    private final PlayerService playerService;

    @Override
    public SendMessage process(Long chatId, Long userId, String text) {
        return playerService.deletePlayer(chatId, userId, text);
    }
}
