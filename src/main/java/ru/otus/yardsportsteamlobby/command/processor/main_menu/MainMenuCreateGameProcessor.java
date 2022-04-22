package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;

@Deprecated
/**
 * Remove after
 */
@Component
@Slf4j
public class MainMenuCreateGameProcessor implements TelegramMessageProcessor {

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        log.error("SHOULD BE EmptyMonthProcessor");
        final var response = new SendMessage();
        response.setText("SHOULD BE EmptyMonthProcessor");
        return response;
    }
}
