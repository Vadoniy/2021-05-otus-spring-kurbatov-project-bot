package ru.otus.yardsportsteamlobby.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.main_menu.MainMenuKeyboardProcessor;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.enums.MainMenuSelect;
import ru.otus.yardsportsteamlobby.repository.BotStateRepository;
import ru.otus.yardsportsteamlobby.repository.redis.BotStateForCurrentUser;
import ru.otus.yardsportsteamlobby.service.cache.CreateGameCache;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InputMessageService {

    private final BotStateRepository botStateRepository;

    private final CreateGameCache createGameCache;

    private final GameService gameService;

    private final List<? extends TelegramMessageProcessor> telegramMessageProcessors;

    private final PlayerService playerService;

    private final PlayerCache playerCache;

    private final UserRoleService userRoleService;

    public SendMessage processInputMessage(@NotNull Message message) {
        final var chatId = message.getChatId();
        final var userId = message.getFrom().getId();
        final var text = message.getText();
        final var usersRole = userRoleService.getUserRoleByUserId(userId);

        if (!botStateRepository.existsById(userId.toString())) {
            return telegramMessageProcessors.stream()
                    .filter(mainMenuProcessor -> mainMenuProcessor.getClass() == MainMenuKeyboardProcessor.class)
                    .findAny()
                    .map(mainMenuProcessor -> mainMenuProcessor.process(chatId, userId, text, usersRole))
                    .orElse(new SendMessage());
        } else {
            final var botState = botStateRepository.findById(userId.toString());
            botState.map(BotStateForCurrentUser::getUserId);
        }

        if (isGameDateTime(text) || createGameCache.isDataAlreadyExists(userId)) {
            return gameService.createGame(chatId, userId, text, usersRole);
        } else if (playerCache.isDataAlreadyExists(userId)) {
            return playerService.registerPlayer(chatId, userId, text, usersRole);
        }

        final var emojiInput = extractEmojiFromInputText(text);
        final var processorClazz = Optional.ofNullable(MainMenuSelect.resolveByEmoji(emojiInput))
                .orElse(MainMenuSelect.MAIN_MENU)
                .getProcessor();

        return telegramMessageProcessors.stream()
                .filter(mainMenuProcessor -> mainMenuProcessor.getClass() == processorClazz)
                .findAny()
                .map(mainMenuProcessor -> mainMenuProcessor.process(chatId, userId, text, usersRole))
                .orElse(new SendMessage());
    }

    private boolean isGameDateTime(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        } else {
            return Stream.of(CallbackQuerySelect.values()).anyMatch(dateState -> text.startsWith(dateState.name() + "_"));
        }
    }

    private String extractEmojiFromInputText(String inputText) {
        return EmojiParser.extractEmojis(inputText).stream().map(EmojiParser::parseToAliases).findAny().orElse("");
    }
}
