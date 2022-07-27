package ru.otus.yardsportsteamlobby.command.processor.callback_quey;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.SignUpForGameCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.SignUpForGameRequest;
import ru.otus.yardsportsteamlobby.repository.redis.SignUpForGameByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.SignUpForGameRequestByUserIdService;

import java.util.Optional;

import static ru.otus.yardsportsteamlobby.enums.BotState.SELECTED_GAME_;
import static ru.otus.yardsportsteamlobby.enums.BotState.SELECTED_TEAM_;

@Service
public class TeamSelectStateProcessor extends SignUpForGameCommonProcessor {

    private final SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    public TeamSelectStateProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                    SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService,
                                    SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService1, YardSportsTeamLobbyClient yardSportsTeamLobbyClient) {
        super(botStateService, keyBoardService, localizationService, signUpForGameRequestByUserIdService);
        this.signUpForGameRequestByUserIdService = signUpForGameRequestByUserIdService1;
        this.yardSportsTeamLobbyClient = yardSportsTeamLobbyClient;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var selectedGameId = signUpForGameRequestByUserIdService.getSignUpForGameRequest(userId)
                .map(SignUpForGameByUserId::getSignUpForGameRequest)
                .map(SignUpForGameRequest::getGameId)
                .orElseThrow();
        final var selectedTeamId = Long.parseLong(text.replace(SELECTED_TEAM_.name(), ""));
        final var responseEntity = yardSportsTeamLobbyClient.signUpForGameRequest(selectedGameId, selectedTeamId, userId);
        final var responseStatus = responseEntity.getStatusCode();
        if (responseStatus == HttpStatus.ALREADY_REPORTED) {
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.select-other-team", userId));
            sendMessage.setReplyMarkup(getTeamRosters(chatId, userId, SELECTED_GAME_.name() + selectedGameId).getReplyMarkup());
        } else if (responseStatus == HttpStatus.NO_CONTENT) {
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.teams-full", userId));
        } else if (responseStatus == HttpStatus.MULTI_STATUS) {
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.who-are-you", userId));
            sendMessage.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRole));
        } else if (responseStatus == HttpStatus.OK) {
            final var afterSelectionGame = Optional.of(responseEntity)
                    .map(HttpEntity::getBody)
                    .orElseThrow();
            final var responseText = localizationService.getLocalizedMessage("one-way.message.you-are-in", userId) + '\n' +
                    teamsRosterText(afterSelectionGame.getTeamA().getTeamName(), afterSelectionGame.getTeamCapacity(),
                            afterSelectionGame.getTeamA().getLineUp()) + '\n' +
                    teamsRosterText(afterSelectionGame.getTeamB().getTeamName(), afterSelectionGame.getTeamCapacity(),
                            afterSelectionGame.getTeamB().getLineUp());
            sendMessage.setText(responseText);
            sendMessage.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRole));
            signUpForGameRequestByUserIdService.removeSignUpForGameRequest(userId);
        }
    }
}
