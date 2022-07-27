package ru.otus.yardsportsteamlobby.command.processor;

import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.dto.GameDto;
import ru.otus.yardsportsteamlobby.dto.PlayerDto;
import ru.otus.yardsportsteamlobby.repository.redis.SignUpForGameByUserId;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.SignUpForGameRequestByUserIdService;

import java.util.ArrayList;
import java.util.List;

import static ru.otus.yardsportsteamlobby.enums.BotState.SELECTED_GAME_;
import static ru.otus.yardsportsteamlobby.enums.BotState.SELECTED_TEAM_;

public abstract class SignUpForGameCommonProcessor extends AbstractCommonProcessor {

    protected final SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService;

    public SignUpForGameCommonProcessor(BotStateService botStateService, KeyBoardService keyBoardService, LocalizationService localizationService,
                                        SignUpForGameRequestByUserIdService signUpForGameRequestByUserIdService) {
        super(botStateService, keyBoardService, localizationService);
        this.signUpForGameRequestByUserIdService = signUpForGameRequestByUserIdService;
    }

    protected SendMessage getTeamRosters(Long chatId, Long userId, String text) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(localizationService.getLocalizedMessage("one-way.message.select-team", userId));
        final var gameId = Long.parseLong(text.replace(SELECTED_GAME_.name(), ""));
        final var signUpForGameByUserId = signUpForGameRequestByUserIdService.getSignUpForGameRequest(userId)
                .orElse(new SignUpForGameByUserId());
        signUpForGameByUserId.getSignUpForGameRequest().setGameId(gameId);
        signUpForGameRequestByUserIdService.saveSignUpForGameRequest(userId, signUpForGameByUserId.getSignUpForGameRequest());
        final var selectedGame = signUpForGameByUserId.getSignUpForGameRequest().getLastGames().stream()
                .filter(gameDto -> gameId == gameDto.getGameId())
                .findFirst();
        final var teamARoster = selectedGame
                .map(GameDto::getTeamA)
                .map(teamA -> teamsRosterText(teamA.getTeamName(), selectedGame.map(GameDto::getTeamCapacity).orElse(0), teamA.getLineUp()))
                .orElse("");
        final var teamBRoster = selectedGame.map(GameDto::getTeamB)
                .map(teamA -> teamsRosterText(teamA.getTeamName(), selectedGame.map(GameDto::getTeamCapacity).orElse(0), teamA.getLineUp()))
                .orElse("");
        selectedGame.map(this::createRosterButtons)
                .map(keyBoardService::createKeyboardMarkup)
                .ifPresent(inlineKeyboardMarkup -> {
                    response.setText(teamARoster + "\n" + teamBRoster);
                    response.setReplyMarkup(inlineKeyboardMarkup);
                });
        return response;
    }

    protected String teamsRosterText(String teamName, int capacity, List<PlayerDto> lineUp) {
        final var sb = new StringBuilder(teamName + ":");

        for (int i = 1; i <= capacity; i++) {
            sb.append("\n").append(i).append(") ");
            if (!CollectionUtils.isEmpty(lineUp) && lineUp.size() >= i) {
                sb.append(lineUp.get(i - 1).getPlayerName());
            }
        }
        return sb.toString();
    }

    protected ArrayList<List<InlineKeyboardButton>> createRosterButtons(GameDto gameDto) {
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        final var teamA = gameDto.getTeamA();
        final var teamB = gameDto.getTeamB();
        final var teamARoster = createTeamRoster(teamA.getTeamId(), teamA.getTeamName());
        final var teamBRoster = createTeamRoster(teamB.getTeamId(), teamB.getTeamName());
        final var teamsButtonsRow = new ArrayList<InlineKeyboardButton>(2);
        teamsButtonsRow.add(teamARoster);
        teamsButtonsRow.add(teamBRoster);
        keyBoardList.add(teamsButtonsRow);
        return keyBoardList;
    }

    protected InlineKeyboardButton createTeamRoster(Long teamId, String teamName) {
        final var rosterButton = new InlineKeyboardButton();
        rosterButton.setText(teamName);
        rosterButton.setCallbackData(SELECTED_TEAM_.name() + teamId);
        return rosterButton;
    }
}
