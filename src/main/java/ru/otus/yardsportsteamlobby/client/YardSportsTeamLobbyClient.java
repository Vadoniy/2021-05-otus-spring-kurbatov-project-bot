package ru.otus.yardsportsteamlobby.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
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

    private static final String GET_USERS_ROLE = "/user/role/{userId}";

    private final RestTemplate apiRestTemplate;

    public String sendCreatePlayerRequest(CreatePlayerRequest createPlayerRequest) {
        return apiRestTemplate.postForObject(NEW_PLAYER, createPlayerRequest, String.class);
    }

    public ResponseEntity<String> sendDeletePlayerRequest(String userId) {
        final var uriVariables = new HashMap<String, String>();
        uriVariables.put("userId", userId);
        final var headers = new HttpHeaders();
        headers.setBasicAuth(userId, userId);
        return apiRestTemplate.exchange(DELETE_PLAYER, HttpMethod.DELETE, new HttpEntity<String>(headers), String.class, uriVariables);
    }

    public String sendCreateGameRequest(CreateGameRequest createGameRequest, Long userId) {
        final var headers = new HttpHeaders();
        headers.setBasicAuth(userId.toString(), userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        final var httpRequestEntity = new HttpEntity<>(createGameRequest, headers);
        return apiRestTemplate.postForObject(NEW_GAME, httpRequestEntity, String.class);
    }

    public ResponseEntity<ListGameResponse> sendGetGameListRequest(Long userId, int amountOfGames) {
        final var headers = new HttpHeaders();
        headers.setBasicAuth(userId.toString(), userId.toString());
        return apiRestTemplate.exchange(GAME_LIST, HttpMethod.GET, new HttpEntity<String>(headers), ListGameResponse.class,
                Map.of("amountOfGames", amountOfGames));
    }

    public ResponseEntity<GameDto> signUpForGameRequest(long gameId, long teamId, long userId) {
        final var mapvars = Map.of("gameId", gameId, "teamId", teamId, "userId", userId);
        final var headers = new HttpHeaders();
        headers.setBasicAuth(String.valueOf(userId), String.valueOf(userId));
        return apiRestTemplate.exchange(NEW_GAME_TEAM_PLAYER, HttpMethod.POST, new HttpEntity<>(headers), GameDto.class, mapvars);
    }

    public ResponseEntity<String> getUsersRoleByUserId(Long userId) {
        final var headers = new HttpHeaders();
        headers.setBasicAuth(userId.toString(), userId.toString());
        return apiRestTemplate.exchange(GET_USERS_ROLE, HttpMethod.GET, new HttpEntity<String>(headers), String.class, userId);
    }
}
