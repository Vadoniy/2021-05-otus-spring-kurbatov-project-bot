package ru.otus.yardsportsteamlobby.command.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;

/**
 * Use TelegramMessageProcessor instead of current
 */
@Deprecated
public interface CreateGameProcessor {

    SendMessage process(GameCreatingStateWithRequest gameData, Long chatId, String text, Long userId, String userRole);
}
