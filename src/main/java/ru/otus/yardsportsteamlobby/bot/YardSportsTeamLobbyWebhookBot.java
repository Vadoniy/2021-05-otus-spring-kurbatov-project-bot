package ru.otus.yardsportsteamlobby.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
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
        final var from = getFrom(update);
        final var text = getText(update);
        final var chatId = getChatId(update);
        final var userId = getUserId(update);
        final var userRole = userRoleService.getUserRoleByUserId(userId);

        log.info(String.format(NEW_RECEIVED_MESSAGE_LOG, from, text));

        return updateService.processIncomingUpdate(chatId, userId, text, userRole);
    }

    @Override
    public String getBotPath() {
        return telegramBotConfigurationProperties.getWebhookPath();
    }

    private User getFrom(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        }
        if (update.getMessage() != null) {
            return update.getMessage().getFrom();
        }
        if (update.getMyChatMember() != null) {
            return update.getMyChatMember().getFrom();
        }
        return null;
    }

    private String getText(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        if (update.getMessage() != null) {
            return update.getMessage().getText();
        }
        return null;
    }

    private Long getChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        if (update.getMessage() != null) {
            return update.getMessage().getChatId();
        }
        if (update.getMyChatMember() != null) {
            return update.getMyChatMember().getChat().getId();
        }
        return null;
    }

    private Long getUserId(Update update) {
        if (update.hasCallbackQuery()) {
            return Optional.ofNullable(update.getCallbackQuery().getFrom())
                    .map(User::getId)
                    .orElse(update.getCallbackQuery().getMessage().getFrom().getId());
        }
        if (update.getMessage() != null) {
            return update.getMessage().getFrom().getId();
        }
        if (update.getMyChatMember() != null) {
            return update.getMyChatMember().getFrom().getId();
        }
        return null;
    }
}
