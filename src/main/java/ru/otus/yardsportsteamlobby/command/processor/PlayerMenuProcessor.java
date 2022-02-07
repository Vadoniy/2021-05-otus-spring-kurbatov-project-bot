package ru.otus.yardsportsteamlobby.command.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;

public interface PlayerMenuProcessor {

    SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId);
}
