package ru.otus.yardsportsteamlobby.service.cache;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class CreateGameCache implements Cache<GameCreatingStateWithRequest> {

    private Map<Long, GameCreatingStateWithRequest> createGameCache = new HashMap<>();

    public GameCreatingStateWithRequest getData(Long userId) {
        return createGameCache.get(userId);
    }

    public GameCreatingStateWithRequest addData(Long userId, GameCreatingStateWithRequest gameCreatingStateWithRequest) {
        return createGameCache.put(userId, gameCreatingStateWithRequest);
    }

    public GameCreatingStateWithRequest removeData(Long userId) {
        return createGameCache.remove(userId);
    }

    public boolean isDataAlreadyExists(Long userId) {
        return createGameCache.containsKey(userId);
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void dropCache() {
        createGameCache = new HashMap<>();
    }
}
