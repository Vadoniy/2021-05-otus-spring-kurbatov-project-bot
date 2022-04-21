package ru.otus.yardsportsteamlobby.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.yardsportsteamlobby.configuration.properties.TelegramBotConfigurationProperties;
import ru.otus.yardsportsteamlobby.service.UpdateService;
import ru.otus.yardsportsteamlobby.service.UserRoleService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YardSportsTeamLobbyWebhookBot extends TelegramWebhookBot {

    private static final String NEW_RECEIVED_MESSAGE_LOG = "Received message from %s: %s";

    private final TelegramBotConfigurationProperties telegramBotConfigurationProperties;

    private final UpdateService updateService;

    private final UserRoleService userRoleService;

    @Override
    public String getBotUsername() {
        return telegramBotConfigurationProperties.getUserName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfigurationProperties.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        final var from = Optional.ofNullable(getFrom(update.getCallbackQuery()))
                .orElse(getFrom(update.getMessage()))
                .orElse("Unknown contact");
        final var text = Optional.ofNullable(getText(update.getCallbackQuery()))
                .orElse(getText(update.getMessage()))
                .orElse("Unknown contact");
        final var chatId = update.getChatMember().getChat().getId();
        final var userId = update.getChatMember().getFrom().getId();
        final var userRole = userRoleService.getUserRoleByUserId(userId);

        log.info(String.format(NEW_RECEIVED_MESSAGE_LOG, from, text));

        return updateService.processIncomingUpdate(chatId, userId, text, userRole);
    }

    @Override
    public String getBotPath() {
        return telegramBotConfigurationProperties.getWebhookPath();
    }

    private Optional<String> getFrom(CallbackQuery callbackQuery) {
        return Optional.ofNullable(callbackQuery)
                .map(CallbackQuery::getFrom)
                .map(User::getUserName);
    }

    private Optional<String> getFrom(Message message) {
        return Optional.ofNullable(message)
                .map(Message::getFrom)
                .map(User::getUserName);
    }

    private Optional<String> getText(CallbackQuery callbackQuery) {
        return Optional.ofNullable(callbackQuery)
                .map(CallbackQuery::getMessage)
                .map(Message::getText);
    }

    private Optional<String> getText(Message message) {
        return Optional.ofNullable(message)
                .map(Message::getText);
    }
}
