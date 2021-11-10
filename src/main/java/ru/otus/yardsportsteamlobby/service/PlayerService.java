package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerPosition;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.enums.YesNoSelect;
import ru.otus.yardsportsteamlobby.service.cache.PlayerCache;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    private final PlayerCache playerCache;

    private final KeyBoardService keyBoardService;

    public SendMessage registerPlayer(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        if (playerCache.isDataAlreadyExists(chatId)) {
            final var userData = playerCache.getData(userId);
            final var request = userData.getCreatePlayerRequest();
            if (PlayerRegistrationState.EMPTY_NAME == userData.getPlayerRegistrationState()) {
                request.setName(text);
                response.setText("Введите 10 или 11 цифр номера телефона для связи (79991234567)");
                userData.setPlayerRegistrationState(PlayerRegistrationState.EMPTY_PHONE);
            } else if (PlayerRegistrationState.EMPTY_PHONE == userData.getPlayerRegistrationState()) {
                if (Pattern.matches("\\d{10,11}", text)) {
                    request.setPhone(text);
                    response.setText("Ваш игровой номер (если отсутствует, поставьте 0)");
                    userData.setPlayerRegistrationState(PlayerRegistrationState.EMPTY_NUMBER);
                } else {
                    response.setText("Неправильный ввод. Введите 10 или 11 цифр номера телефона для связи (79991234567)");
                }
            } else if (PlayerRegistrationState.EMPTY_NUMBER == userData.getPlayerRegistrationState()) {
                try {
                    request.setNumber(Integer.parseInt(text));
                    response.setText("На какой позиции играете?");
                    response.setReplyMarkup(keyBoardService.createSelectPositionMarkup());
                    userData.setPlayerRegistrationState(PlayerRegistrationState.EMPTY_POSITION);
                } catch (NumberFormatException nfe) {
                    response.setText("Что-то не так с номером, попробуйте ещё раз. Просто цифры.");
                }
            } else if (PlayerRegistrationState.EMPTY_POSITION == userData.getPlayerRegistrationState()) {
                request.setPosition(PlayerPosition.valueOf(text));
                request.setUserId(userId);
                final var apiResponse = yardSportsTeamLobbyClient.sendCreatePlayerRequest(request);
                response.setText("Запрос на обновление данных отправлен");
                if (StringUtils.hasText(apiResponse)) {
                    response.setText(apiResponse);
                    playerCache.removeData(userId);
                }
                response.setReplyMarkup(keyBoardService.createMainMenuKeyboard());
            }
        } else {
            playerCache.addData(userId, new RegistrationStateWithRequest()
                    .setPlayerRegistrationState(PlayerRegistrationState.EMPTY_NAME)
                    .setCreatePlayerRequest(new CreatePlayerRequest()));
            response.setText("Введите имя");
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
