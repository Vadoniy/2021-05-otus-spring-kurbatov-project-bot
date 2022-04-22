package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.enums.MainMenuButtonName;
import ru.otus.yardsportsteamlobby.repository.redis.BotStateForCurrentUser;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateService {

    private final BotStateService botStateService;

    private final LocalizationService localizationService;

    private final List<TelegramMessageProcessor> telegramMessageProcessors;

    public SendMessage processIncomingUpdate(Long chatId, Long userId, String text, String userRole) {

        localizationService.getLocalizedMessages("main.menu.", userId).entrySet().stream()
                .filter(stringStringEntry -> StringUtils.equals(text, stringStringEntry.getValue()))
                .map(stringStringEntry -> MainMenuButtonName.fromValue(stringStringEntry.getKey()))
                .findAny()
                .ifPresent(mainMenuButtonName -> botStateService.saveBotStateForUser(userId, mainMenuButtonName.getBotState()));

        final var currentBotState = Optional.ofNullable(botStateService.getCurrentBotState(userId))
                .map(BotStateForCurrentUser::getBotState)
                .orElse(BotState.MAIN_MENU);

        return telegramMessageProcessors.stream()
                .filter(telegramMessageProcessor -> telegramMessageProcessor.getClass() == currentBotState.getProcessor())
                .findAny()
                .map(telegramMessageProcessor -> telegramMessageProcessor.process(chatId, userId, text, userRole))
                .orElse(new SendMessage());
    }
}
