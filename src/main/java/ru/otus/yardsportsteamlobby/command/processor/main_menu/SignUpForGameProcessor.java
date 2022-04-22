package ru.otus.yardsportsteamlobby.command.processor.main_menu;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.SignUpForGameCommonProcessor;
import ru.otus.yardsportsteamlobby.dto.GameDto;
import ru.otus.yardsportsteamlobby.dto.ListGameResponse;
import ru.otus.yardsportsteamlobby.dto.SignUpDto;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.SignUpForGameRequestByUserIdService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SignUpForGameProcessor extends SignUpForGameCommonProcessor {

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    public SignUpForGameProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                  SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService, YardSportsTeamLobbyClient yardSportsTeamLobbyClient) {
        super(botStateService, keyBoardService, localizationService, signUpForGameRequestByUserIdService);
        this.yardSportsTeamLobbyClient = yardSportsTeamLobbyClient;
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        final var listGameResponse = yardSportsTeamLobbyClient.sendGetGameListRequest(userId, 3);
        final var lastGames = Optional.of(listGameResponse)
                .map(ResponseEntity::getBody)
                .map(ListGameResponse::getGames)
                .orElse(new ArrayList<>(0));

        if (CollectionUtils.isEmpty(lastGames)) {
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.no-games", userId));
            sendMessage.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRole));
        } else {
            final var gameList = createGameButtonsList(lastGames);
            signUpForGameRequestByUserIdService.addData(userId, new SignUpDto().setLastGames(lastGames));
            sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.select-game", userId));
            sendMessage.setReplyMarkup(keyBoardService.createKeyboardMarkup(gameList));
        }
    }

    private ArrayList<List<InlineKeyboardButton>> createGameButtonsList(List<GameDto> lastGames) {
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(lastGames.size());
        for (GameDto gameDto : lastGames) {
            final var anotherRow = new ArrayList<InlineKeyboardButton>(1);
            final var anotherGame = new InlineKeyboardButton();
            anotherGame.setText(gameDto.getGameDateTime().toString()
                    + ": " + gameDto.getTeamA().getTeamName()
                    + " - " + gameDto.getTeamB().getTeamName());
            anotherGame.setCallbackData(CallbackQuerySelect.SELECTED_GAME_.name() + gameDto.getGameId());
            anotherRow.add(anotherGame);
            keyBoardList.add(anotherRow);
        }
        return keyBoardList;
    }
}
