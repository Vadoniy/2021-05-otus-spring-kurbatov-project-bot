package ru.otus.yardsportsteamlobby.service.cache;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;

import java.util.HashMap;

@Service
@Getter
public class PlayerCache implements Cache<RegistrationStateWithRequest> {

    private HashMap<Long, RegistrationStateWithRequest> playerCache = new HashMap<>();

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
    private void dropPlayerCache() {
        playerCache = new HashMap<>();
    }
}
