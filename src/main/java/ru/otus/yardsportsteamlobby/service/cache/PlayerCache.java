package ru.otus.yardsportsteamlobby.service.cache;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
public class PlayerCache implements Cache<RegistrationStateWithRequest> {

    private Map<Long, RegistrationStateWithRequest> playerCache = new ConcurrentHashMap<>();

    public RegistrationStateWithRequest getData(Long userId) {
        return playerCache.get(userId);
    }

    public RegistrationStateWithRequest addData(Long userId, RegistrationStateWithRequest registrationStateWithRequest) {
        return playerCache.put(userId, registrationStateWithRequest);
    }

    public RegistrationStateWithRequest removeData(Long userId) {
        return playerCache.remove(userId);
    }

    public boolean isDataAlreadyExists(Long userId) {
        return playerCache.containsKey(userId);
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void dropCache() {
        playerCache = new ConcurrentHashMap<>();
    }
}
