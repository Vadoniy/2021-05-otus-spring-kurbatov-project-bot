package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.enums.MainMenuSelect;
import ru.otus.yardsportsteamlobby.service.cache.CreateGameCache;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InputMessageService {

    private final ConfigurableApplicationContext context;

    private final CreateGameCache createGameCache;

    private final GameService gameService;

    private final LocalizationService localizationService;

    private final PlayerService playerService;

    private final PlayerCache playerCache;

    public SendMessage processInputMessage(@NotNull Message message) {
        final var chatId = message.getChatId();
        final var userId = message.getFrom().getId();
        final var text = message.getText();

        if (isGameDateTime(text) || createGameCache.isDataAlreadyExists(userId)) {
            return gameService.createGame(chatId, userId, text);
        } else if (playerCache.isDataAlreadyExists(userId)) {
            return playerService.registerPlayer(chatId, userId, message.getText());
        }

        final var processorClazz = Stream.of(MainMenuSelect.values())
                .filter(mainMenuSelect -> localizationService.getLocalizedMessage(mainMenuSelect.getMessage()).equals(text))
                .findAny()
                .orElse(MainMenuSelect.START)
                .getProcessor();
        final var processor = context.getBean(processorClazz);
        return processor.process(chatId, userId, text);
    }

    private boolean isGameDateTime(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        } else {
            return Stream.of(CallbackQuerySelect.values()).anyMatch(dateState -> text.startsWith(dateState.name() + "_"));
        }
    }
}
