package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.DeletePlayerProcessor;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final CreatePlayerRequestByUserIdService createPlayerRequestByUserIdService;

    private final LocalizationService localizationService;

    private final PlayerCache playerCache;

    private final List<? extends TelegramMessageProcessor> playerMenuProcessors;

    private final KeyBoardService keyBoardService;

    public SendMessage registerPlayer(Long chatId, Long userId, String text, String userRole) {
        if (createPlayerRequestByUserIdService.isDataAlreadyExists(chatId)) {
            final var registrationState = playerCache.getData(userId).getPlayerRegistrationState();
            final var processor = playerMenuProcessors.stream()
                    .filter(createGameProcessor -> createGameProcessor.getClass() == registrationState.getProcessor())
                    .findAny();

            if (processor.isPresent()) {
                return processor.map(playerMenuProcessor -> playerMenuProcessor.process(userId, chatId, text, userRole))
                        .orElse(new SendMessage());
            } else {
                playerCache.removeData(userId);
                return new SendMessage();
            }
        } else {
            return processNewMessage(chatId, userId, "one-way.message.enter-your-name", PlayerRegistrationState.EMPTY_NAME);
        }
    }

    public SendMessage deletePlayer(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text, String userRole) {
        if (playerCache.isDataAlreadyExists(userId)) {
            return processAlreadyExistData(chatId, userId, text, userRole);
        } else {
            final var response = processNewMessage(chatId, userId, "one-way.message.sure", PlayerRegistrationState.DELETE);
            response.setReplyMarkup(keyBoardService.createSelectYesNoMarkup(userId));
            return response;
        }
    }

    private SendMessage processAlreadyExistData(Long chatId, Long userId, String text, String userRole) {
        return playerMenuProcessors.stream()
                .filter(createGameProcessor -> createGameProcessor.getClass() == DeletePlayerProcessor.class)
                .map(playerMenuProcessor1 -> playerMenuProcessor1.process(userId, chatId, text, userRole))
                .findAny()
                .orElse(new SendMessage());
    }

    private SendMessage processNewMessage(Long chatId, Long userId, String textPath, PlayerRegistrationState state) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(localizationService.getLocalizedMessage(textPath, userId));
        final var registrationStateWithRequest = new RegistrationStateWithRequest()
                .setPlayerRegistrationState(state);
        if (state == PlayerRegistrationState.EMPTY_NAME) {
            registrationStateWithRequest.setCreatePlayerRequest(new CreatePlayerRequest());
        }
        playerCache.addData(userId, registrationStateWithRequest);
        return response;
    }
}
