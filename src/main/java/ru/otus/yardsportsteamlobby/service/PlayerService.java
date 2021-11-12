package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.enums.YesNoSelect;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final ConfigurableApplicationContext context;

    private final LocalizationService localizationService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    private final PlayerCache playerCache;

    private final KeyBoardService keyBoardService;

    public SendMessage registerPlayer(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        if (playerCache.isDataAlreadyExists(chatId)) {
            final var userData = playerCache.getData(userId);
            final var processor = context.getBean(PlayerRegistrationState.valueOf(userData.getPlayerRegistrationState().name()).getProcessor());
            return processor.process(userData, chatId, text, userId);
        } else {
            playerCache.addData(userId, new RegistrationStateWithRequest()
                    .setPlayerRegistrationState(PlayerRegistrationState.EMPTY_NAME)
                    .setCreatePlayerRequest(new CreatePlayerRequest()));
            response.setText(localizationService.getLocalizedMessage("one-way.message.enter-your-name"));
        }
        return response;
    }

    public SendMessage deletePlayer(@NotNull Long chatId, @NotNull Long userId, YesNoSelect yesNoSelect) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (playerCache.isDataAlreadyExists(userId)) {
            final var userData = playerCache.getData(userId);
            if (PlayerRegistrationState.DELETE == userData.getPlayerRegistrationState()) {
                if (YesNoSelect.YES == yesNoSelect) {
                    yardSportsTeamLobbyClient.sendDeletePlayerRequest(userId.toString());
                    response.setText("Запрос на удаление данных игрока был отправлен.");
                } else {
                    response.setText("Запрос на удаление данных игрока был удалён.");
                }
                playerCache.removeData(userId);
            }
        } else {
            final var registrationStateWithRequest = new RegistrationStateWithRequest()
                    .setPlayerRegistrationState(PlayerRegistrationState.DELETE);
            playerCache.addData(userId, registrationStateWithRequest);
            response.setReplyMarkup(keyBoardService.createSelectYesNoMarkup());
            response.setText("Вы уверены?");
        }
        return response;
    }
}
