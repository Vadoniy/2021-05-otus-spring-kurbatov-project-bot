package ru.otus.yardsportsteamlobby.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.dto.GameDto;
import ru.otus.yardsportsteamlobby.dto.ListGameResponse;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class YardSportsTeamLobbyClient {

    private static final String NEW_PLAYER = "/player/new";

    private static final String DELETE_PLAYER = "/player/{userId}";

    private static final String NEW_GAME = "/game/new";

    private static final String GAME_LIST = "/game/list/{amountOfGames}";

    private static final String NEW_GAME_TEAM_PLAYER = "/game/{gameId}/team/{teamId}/player/{userId}";

    private final RestTemplate apiRestTemplate;

    public String sendCreatePlayerRequest(CreatePlayerRequest createPlayerRequest) {
        return apiRestTemplate.postForObject(NEW_PLAYER, createPlayerRequest, String.class);
    }

    public void sendDeletePlayerRequest(String userId) {
        final var uriVariables = new HashMap<String, String>();
        uriVariables.put("userId", userId);
        apiRestTemplate.delete(DELETE_PLAYER, uriVariables);
    }

    public String sendCreateGameRequest(CreateGameRequest createGameRequest) {
        return apiRestTemplate.postForObject(NEW_GAME, createGameRequest, String.class);
    }

    public ListGameResponse sendGetGameListRequest(int amountOfGames) {
        return apiRestTemplate.getForObject(GAME_LIST, ListGameResponse.class, Map.of("amountOfGames", amountOfGames));
    }

    public ResponseEntity<GameDto> signUpForGameRequest(long gameId, long teamId, long userId) {
        final var mapvars = Map.of("gameId", gameId, "teamId", teamId, "userId", userId);
        return apiRestTemplate.postForEntity(NEW_GAME_TEAM_PLAYER, null, GameDto.class, mapvars);
    }
}
