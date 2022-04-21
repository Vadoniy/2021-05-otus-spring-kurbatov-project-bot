package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryService {

    private final List<? extends TelegramMessageProcessor> telegramMessageProcessors;

    private final KeyBoardService keyBoardService;

    private final UserRoleService userRoleService;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQueryButton) {
        final var chatId = callbackQueryButton.getMessage().getChatId();
        final var userId = callbackQueryButton.getFrom().getId();
        final var callbackData = callbackQueryButton.getData();
        final var usersRole = userRoleService.getUserRoleByUserId(userId);
        final var processorClazz = CallbackQuerySelect.getProcessorByCallbackData(callbackData);

        return telegramMessageProcessors.stream()
                .filter(callbackQueryProcessor -> callbackQueryProcessor.getClass() == processorClazz)
                .findAny()
                .map(callbackQueryProcessor -> callbackQueryProcessor.process(chatId, userId, callbackData, usersRole))
                .orElse(keyBoardService.createMainMenuKeyboardMessage(chatId, usersRole));
    }
}
