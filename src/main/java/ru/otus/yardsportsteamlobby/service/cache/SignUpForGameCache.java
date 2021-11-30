package ru.otus.yardsportsteamlobby.service.cache;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.SignUpDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
public class SignUpForGameCache implements Cache<SignUpDto> {

    private Map<Long, SignUpDto> signUpForGameCache = new ConcurrentHashMap<>();

    public SignUpDto getData(Long userId) {
        return signUpForGameCache.get(userId);
    }

    public SignUpDto addData(Long userId, SignUpDto signUpDto) {
        return signUpForGameCache.put(userId, signUpDto);
    }

    public SignUpDto removeData(Long userId) {
        return signUpForGameCache.remove(userId);
    }

    public boolean isDataAlreadyExists(Long userId) {
        return signUpForGameCache.containsKey(userId);
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void dropCache() {
        signUpForGameCache = new ConcurrentHashMap<>();
    }
}
