package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.command.processor.TelegramMessageProcessor;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;
import ru.otus.yardsportsteamlobby.dto.GameDto;
import ru.otus.yardsportsteamlobby.dto.ListGameResponse;
import ru.otus.yardsportsteamlobby.dto.PlayerDto;
import ru.otus.yardsportsteamlobby.dto.SignUpDto;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;
import ru.otus.yardsportsteamlobby.service.cache.CreateGameCache;
import ru.otus.yardsportsteamlobby.service.cache.SignUpForGameCache;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.otus.yardsportsteamlobby.enums.CreateGameState.EMPTY_DATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final CalendarService calendarService;

    private final CreateGameCache createGameCache;

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    private final SignUpForGameCache signUpForGameCache;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    private final List<? extends TelegramMessageProcessor> createGameProcessors;

    public SendMessage createGame(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        if (createGameCache.isDataAlreadyExists(userId)) {
            final var gameData = createGameCache.getData(userId);
            final var createGameState = gameData.getCreateGameState();
            final var processor = createGameProcessors.stream()
                    .filter(createGameProcessor -> createGameProcessor.getClass() == createGameState.getProcessor())
                    .findAny();

            if (processor.isPresent()) {
                return processor.map(createGameProcessor -> createGameProcessor.process(chatId, userId, text, userRole)).orElse(new SendMessage());
            } else {
                createGameCache.removeData(userId);
            }
        } else {
            createGameCache.addData(userId, new GameCreatingStateWithRequest()
                    .setCreateGameState(EMPTY_DATE)
                    .setCreateGameRequest(new CreateGameRequest()));
            final var daysOfMonthList = calendarService.fillDaysOfMonth(LocalDate.now().getMonth());
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(daysOfMonthList));
            response.setText(localizationService.getLocalizedMessage("one-way.message.select-date", userId));
        }
        return response;
    }

    public SendMessage signUpForGame(Long chatId, Long userId, String text, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        final var selectedGameId = signUpForGameCache.getData(userId).getSelectedGameId();
        final var selectedTeamId = Long.parseLong(text.replace(CallbackQuerySelect.SELECTED_TEAM_.name(), ""));
        final var responseEntity = yardSportsTeamLobbyClient.signUpForGameRequest(selectedGameId, selectedTeamId, userId);
        final var responseStatus = responseEntity.getStatusCode();
        if (responseStatus == HttpStatus.ALREADY_REPORTED) {
            response.setText(localizationService.getLocalizedMessage("one-way.message.select-other-team", userId));
            response.setReplyMarkup(getTeamRosters(chatId, userId, CallbackQuerySelect.SELECTED_GAME_.name() + selectedGameId).getReplyMarkup());
        } else if (responseStatus == HttpStatus.NO_CONTENT) {
            response.setText(localizationService.getLocalizedMessage("one-way.message.teams-full", userId));
        } else if (responseStatus == HttpStatus.MULTI_STATUS) {
            response.setText(localizationService.getLocalizedMessage("one-way.message.who-are-you", userId));
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRole));
        } else if (responseStatus == HttpStatus.OK) {
            final var afterSelectionGame = Optional.of(responseEntity)
                    .map(HttpEntity::getBody)
                    .orElseThrow();
            final var responseText = localizationService.getLocalizedMessage("one-way.message.you-are-in", userId) + '\n' +
                    teamsRosterText(afterSelectionGame.getTeamA().getTeamName(), afterSelectionGame.getTeamCapacity(),
                            afterSelectionGame.getTeamA().getLineUp()) + '\n' +
                    teamsRosterText(afterSelectionGame.getTeamB().getTeamName(), afterSelectionGame.getTeamCapacity(),
                            afterSelectionGame.getTeamB().getLineUp());
            response.setText(responseText);
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRole));
            signUpForGameCache.removeData(userId);
        }
        return response;
    }

    public SendMessage getTeamRosters(Long chatId, Long userId, String text) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(localizationService.getLocalizedMessage("one-way.message.select-team", userId));
        final var gameId = Long.parseLong(text.replace(CallbackQuerySelect.SELECTED_GAME_.name(), ""));
        signUpForGameCache.getData(userId).setSelectedGameId(gameId);
        final var signUpData = signUpForGameCache.getData(userId);
        final var selectedGame = signUpData.getLastGames().stream()
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

    public SendMessage getGameList(Long chatId, Long userId, String userRole) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        final var listGameResponse = yardSportsTeamLobbyClient.sendGetGameListRequest(userId, 3);
        final var lastGames = Optional.of(listGameResponse)
                .map(ResponseEntity::getBody)
                .map(ListGameResponse::getGames)
                .orElse(new ArrayList<>(0));

        if (CollectionUtils.isEmpty(lastGames)) {
            response.setText(localizationService.getLocalizedMessage("one-way.message.no-games", userId));
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRole));
        } else {
            final var gameList = createGameButtonsList(lastGames);
            signUpForGameCache.addData(userId, new SignUpDto().setLastGames(lastGames));
            response.setText(localizationService.getLocalizedMessage("one-way.message.select-game", userId));
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(gameList));
        }
        return response;
    }

    private ArrayList<List<InlineKeyboardButton>> createRosterButtons(GameDto gameDto) {
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

    private InlineKeyboardButton createTeamRoster(Long teamId, String teamName) {
        final var rosterButton = new InlineKeyboardButton();
        rosterButton.setText(teamName);
        rosterButton.setCallbackData(CallbackQuerySelect.SELECTED_TEAM_.name() + teamId);
        return rosterButton;
    }

    private String teamsRosterText(String teamName, int capacity, List<PlayerDto> lineUp) {
        final var sb = new StringBuilder(teamName + ":");

        for (int i = 1; i <= capacity; i++) {
            sb.append("\n").append(i).append(") ");
            if (lineUp.size() >= i) {
                sb.append(lineUp.get(i - 1).getPlayerName());
            }
        }
        return sb.toString();
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
