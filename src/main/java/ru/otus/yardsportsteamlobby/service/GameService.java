package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.dto.*;
import ru.otus.yardsportsteamlobby.enums.DateState;
import ru.otus.yardsportsteamlobby.enums.GameSelectState;
import ru.otus.yardsportsteamlobby.enums.SkipSelect;
import ru.otus.yardsportsteamlobby.service.cache.CreateGameCache;
import ru.otus.yardsportsteamlobby.service.cache.SignUpForGameCache;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.otus.yardsportsteamlobby.enums.GameCreatingState.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final CalendarService calendarService;

    private final CreateGameCache createGameCache;

    private final SignUpForGameCache signUpForGameCache;

    private final KeyBoardService keyBoardService;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    public SendMessage createGame(@NotNull Long chatId, @NotNull Long userId, @NotBlank String text) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        if (createGameCache.isDataAlreadyExists(userId)) {
            final var gameData = createGameCache.getData(userId);
            if (EPMTY_DATE == gameData.getGameCreatingState()) {
                if (StringUtils.hasText(text) && text.startsWith(DateState.SELECTED_MONTH_.name())) {
                    final var daysOfMonthList = calendarService.fillDaysOfMonth(Month.valueOf(text.replace(DateState.SELECTED_MONTH_.name(), "")));
                    response.setReplyMarkup(keyBoardService.createKeyboardMarkup(daysOfMonthList));
                    response.setText("Выберите дату");
                    return response;
                }
                final var request = gameData.getCreateGameRequest();
                final var gameDateTime = LocalDate.parse(text.replace(DateState.SELECTED_DATE_.name(), "")).atStartOfDay();
                request.setGameDateTime(gameDateTime);
                gameData.setGameCreatingState(EMPTY_TIME);
                response.setText("Укажите время игры в формате HH:mm (например, 21:15).");
            } else if (EMPTY_TIME == gameData.getGameCreatingState()) {
                if (!isTimeInputOk(text)) {
                    response.setText("Неправильный формат времени, укажите время игры в формате HH:mm (например, 09:15 или 21:15).");
                } else {
                    final var gameTime = LocalTime.parse(text);
                    final var gameDate = gameData.getCreateGameRequest().getGameDateTime().toLocalDate();
                    final var gameDateTime = LocalDateTime.of(gameDate, gameTime);
                    gameData.getCreateGameRequest().setGameDateTime(gameDateTime);
                    gameData.setGameCreatingState(EMPTY_CAPACITY);
                    response.setText("Введите количество игроков в команде (например, 6)");
                }
            } else if (EMPTY_CAPACITY == gameData.getGameCreatingState()) {
                if (!StringUtils.hasText(text) || !text.matches("\\d{1,2}")) {
                    response.setText("Введите количество игроков в команде (например, 6)");
                } else {
                    final var teamCapacity = Integer.parseInt(text);
                    gameData.getCreateGameRequest().setTeamCapacity(teamCapacity);
                    gameData.setGameCreatingState(EMPTY_TEAM_1_NAME);
                    response.setText("Введите имя команды А или нажмите пропустить (будет выбрано имя по умолчанию)");
                    response.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton()));
                }
            } else if (EMPTY_TEAM_1_NAME == gameData.getGameCreatingState()) {
                if (StringUtils.hasText(text) && !SkipSelect.SKIP.name().equals(text)) {
                    gameData.getCreateGameRequest().setTeamNameA(text);
                }
                gameData.setGameCreatingState(EMPTY_TEAM_2_NAME);
                response.setText("Введите имя команды Б или нажмите пропустить (будет выбрано имя по умолчанию)");
                response.setReplyMarkup(keyBoardService.createKeyboardMarkup(createSkipButton()));
            } else if (EMPTY_TEAM_2_NAME == gameData.getGameCreatingState()) {
                if (StringUtils.hasText(text) && !SkipSelect.SKIP.name().equals(text)) {
                    gameData.getCreateGameRequest().setTeamNameB(text);
                }
                final var apiResponse = yardSportsTeamLobbyClient.sendCreateGameRequest(gameData.getCreateGameRequest());
                response.setText("Запрос на создание отправлен");
                if (StringUtils.hasText(apiResponse)) {
                    response.setText(apiResponse);
                    createGameCache.removeData(userId);
                }
                response.setReplyMarkup(keyBoardService.createMainMenuKeyboard());
            }
        } else {
            createGameCache.addData(userId, new GameCreatingStateWithRequest()
                    .setGameCreatingState(EPMTY_DATE)
                    .setCreateGameRequest(new CreateGameRequest()));
            final var daysOfMonthList = calendarService.fillDaysOfMonth(LocalDate.now().getMonth());
            response.setReplyMarkup(keyBoardService.createKeyboardMarkup(daysOfMonthList));
            response.setText("Выберите дату");
        }
        return response;
    }

    public SendMessage signUpForGame(Long chatId, Long userId, String text) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        final var selectedGameId = signUpForGameCache.getData(userId).getSelectedGameId();
        final var selectedTeamId = Long.parseLong(text.replace(GameSelectState.SELECTED_TEAM_.name(), ""));
        final var responseEntity = yardSportsTeamLobbyClient.signUpForGameRequest(selectedGameId, selectedTeamId, userId);
        final var responseStatus = responseEntity.getStatusCode();
        if (responseStatus == HttpStatus.ALREADY_REPORTED) {
            response.setText("Выбранная команда полностью укомплектована, выберите другую");
            response.setReplyMarkup(getTeamRosters(chatId, userId, GameSelectState.SELECTED_GAME_.name() + selectedGameId).getReplyMarkup());
        } else if (responseStatus == HttpStatus.NO_CONTENT) {
            response.setText("В командах нет места");
        } else if (responseStatus == HttpStatus.OK) {
            final var afterSelectionGame = Optional.of(responseEntity)
                    .map(HttpEntity::getBody)
                    .orElseThrow();
            final var responseText = new StringBuilder("Ура! Вы в игре!\n");
            responseText
                    .append(
                            teamsRosterText(afterSelectionGame.getTeamA().getTeamName(), afterSelectionGame.getTeamCapacity(),
                                    afterSelectionGame.getTeamA().getLineUp()))
                    .append('\n')
                    .append(teamsRosterText(afterSelectionGame.getTeamB().getTeamName(), afterSelectionGame.getTeamCapacity(),
                            afterSelectionGame.getTeamB().getLineUp()));
            response.setText(responseText.toString());
            response.setReplyMarkup(keyBoardService.createMainMenuKeyboard());
            signUpForGameCache.removeData(userId);
        }
        return response;
    }

    public SendMessage getTeamRosters(Long chatId, Long userId, String text) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("Выберите команду:");
        final var gameId = Long.parseLong(text.replace(GameSelectState.SELECTED_GAME_.name(), ""));
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

    public SendMessage getGameList(Long chatId, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());

        final var listGameResponse = yardSportsTeamLobbyClient.sendGetGameListRequest(3);
        final var lastGames = listGameResponse.getGames();

        if (CollectionUtils.isEmpty(lastGames)) {
            response.setText("Игр пока что не было и не запланировано.");
        } else {
            final var gameList = createGameButtonsList(lastGames);
            signUpForGameCache.addData(userId, new SignUpDto().setLastGames(lastGames));
            response.setText("Выберите игру:");
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

    private InlineKeyboardButton createTeamRoster(Long teamId, String teamName) {
//        final var sb = new StringBuilder(teamName + ":");
//
//        for (int i = 1; i <= capacity; i++) {
//            sb.append("\n").append(i).append(") ");
//            if (lineUp.size() >= i) {
//                sb.append(lineUp.get(i).getPlayerName());
//            }
//        }

        final var rosterButton = new InlineKeyboardButton();
        rosterButton.setText(teamName);
        rosterButton.setCallbackData(GameSelectState.SELECTED_TEAM_.name() + teamId);
        return rosterButton;
    }

    private ArrayList<List<InlineKeyboardButton>> createGameButtonsList(List<GameDto> lastGames) {
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(lastGames.size());
        for (GameDto gameDto : lastGames) {
            final var anotherRow = new ArrayList<InlineKeyboardButton>(1);
            final var anotherGame = new InlineKeyboardButton();
            anotherGame.setText(gameDto.getGameDateTime().toString()
                    + ": " + gameDto.getTeamA().getTeamName()
                    + " - " + gameDto.getTeamB().getTeamName());
            anotherGame.setCallbackData(GameSelectState.SELECTED_GAME_.name() + gameDto.getGameId());
            anotherRow.add(anotherGame);
            keyBoardList.add(anotherRow);
        }
        return keyBoardList;
    }

    private ArrayList<List<InlineKeyboardButton>> createSkipButton() {
        final var skipButton = new InlineKeyboardButton();
        skipButton.setText("Пропустить");
        skipButton.setCallbackData(SkipSelect.SKIP.name());
        final var keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(skipButton);
        final var keyBoardList = new ArrayList<List<InlineKeyboardButton>>(1);
        keyBoardList.add(keyboardButtonsRow1);
        return keyBoardList;
    }

    private boolean isTimeInputOk(String inputTime) {
        if (!StringUtils.hasText(inputTime) || !inputTime.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        final var timeUnits = inputTime.split(":");
        final var hour = Integer.parseInt(timeUnits[0]);
        final var minute = Integer.parseInt(timeUnits[1]);
        return hour < 24 && hour > 0 && minute < 60 && minute > 0;
    }
}
