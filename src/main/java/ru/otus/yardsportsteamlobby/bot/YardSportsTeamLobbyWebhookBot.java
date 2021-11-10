package ru.otus.yardsportsteamlobby.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.yardsportsteamlobby.configuration.properties.TelegramBotConfigurationProperties;
import ru.otus.yardsportsteamlobby.service.CallbackQueryService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YardSportsTeamLobbyWebhookBot extends TelegramWebhookBot {

    private static final String NEW_RECEIVED_MESSAGE_LOG = "Received message from %s: %s";

    private final CallbackQueryService callbackQueryService;

    private final TelegramBotConfigurationProperties telegramBotConfigurationProperties;

    @Override
    public String getBotUsername() {
        return telegramBotConfigurationProperties.getUserName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfigurationProperties.getBotToken();
    }

    @Override
    public void onRegister() {

    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            final var from = Optional.ofNullable(update.getCallbackQuery())
                    .map(CallbackQuery::getFrom)
                    .map(User::getUserName)
                    .orElse("Unknown contact");
            final var text = Optional.ofNullable(update.getCallbackQuery())
                    .map(CallbackQuery::getMessage)
                    .map(Message::getText)
                    .orElse("Empty message");
            log.info(String.format(NEW_RECEIVED_MESSAGE_LOG, from, text));
            return callbackQueryService.processCallbackQuery(update.getCallbackQuery());
        } else if (update.getMessage() != null) {
            final var from = Optional.ofNullable(update.getMessage())
                    .map(Message::getFrom)
                    .map(User::getUserName)
                    .orElse("Unknown contact");
            final var text = Optional.ofNullable(update.getMessage())
                    .map(Message::getText)
                    .orElse("Empty message");
            log.info(String.format(NEW_RECEIVED_MESSAGE_LOG, from, text));
            return callbackQueryService.processInputMessage(update.getMessage());
        } else {
            return new SendMessage("", "Empty data");
        }
    }

    @Override
    public String getBotPath() {
        return telegramBotConfigurationProperties.getWebhookPath();
    }
}
