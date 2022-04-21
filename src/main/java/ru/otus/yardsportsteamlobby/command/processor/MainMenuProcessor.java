package ru.otus.yardsportsteamlobby.command.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Use TelegramMessageProcessor instead of current
 */
@Deprecated
public interface MainMenuProcessor {

    SendMessage process(Long chatId, Long userId, String text, String userRole);
}
