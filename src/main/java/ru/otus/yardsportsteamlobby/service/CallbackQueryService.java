package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.yardsportsteamlobby.command.processor.CallbackQueryProcessor;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryService {

    private final ConfigurableApplicationContext context;

    private final KeyBoardService keyBoardService;

    private final UserRoleService userRoleService;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQueryButton) {
        final var chatId = callbackQueryButton.getMessage().getChatId();
        final var userId = callbackQueryButton.getFrom().getId();
        final var callbackData = callbackQueryButton.getData();
        final var usersRole = userRoleService.getUserRoleByUserId(userId);

        return Optional.ofNullable(CallbackQuerySelect.getProcessorByCallbackData(callbackData))
                .map(processorClazz -> (CallbackQueryProcessor) context.getBean(processorClazz))
                .map(processor -> processor.process(chatId, userId, callbackData, usersRole))
                .orElse(keyBoardService.createMainMenuKeyboardMessage(chatId, usersRole));
    }
}
