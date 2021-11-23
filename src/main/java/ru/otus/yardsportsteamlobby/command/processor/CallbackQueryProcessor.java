package ru.otus.yardsportsteamlobby.command.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CallbackQueryProcessor {

    SendMessage process(Long chatId, Long userId, String text, String userRole);
}
