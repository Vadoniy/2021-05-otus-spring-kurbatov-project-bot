package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.command.processor.player_menu.DeletePlayerProcessor;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final ConfigurableApplicationContext context;

    private final LocalizationService localizationService;

    private final PlayerCache playerCache;

    private final KeyBoardService keyBoardService;

    public SendMessage registerPlayer(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text) {
        if (playerCache.isDataAlreadyExists(chatId)) {
            final var registrationState = playerCache.getData(userId).getPlayerRegistrationState();
            final var processorClazz = PlayerRegistrationState.valueOf(registrationState.name()).getProcessor();
            return processAlreadyExistData(chatId, userId, text, processorClazz);
        } else {
            return processNewMessage(chatId, userId, "one-way.message.enter-your-name", PlayerRegistrationState.EMPTY_NAME);
        }
    }

    public SendMessage deletePlayer(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text) {
        if (playerCache.isDataAlreadyExists(userId)) {
            return processAlreadyExistData(chatId, userId, text, DeletePlayerProcessor.class);
        } else {
            final var response = processNewMessage(chatId, userId, "one-way.message.sure", PlayerRegistrationState.DELETE);
            response.setReplyMarkup(keyBoardService.createSelectYesNoMarkup(userId));
            return response;
        }
    }

    private SendMessage processAlreadyExistData(Long chatId, Long userId, String text, Class<? extends PlayerMenuProcessor> playerMenuProcessor) {
        final var userData = playerCache.getData(userId);
        final var processor = context.getBean(playerMenuProcessor);
        return processor.process(userData, chatId, text, userId);
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
