package ru.otus.yardsportsteamlobby.command.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;

/**
 * Use TelegramMessageProcessor instead of current
 */
@Deprecated
public interface PlayerMenuProcessor {

    SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId, String userRole);
}
