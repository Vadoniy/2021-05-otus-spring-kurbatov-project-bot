package ru.otus.yardsportsteamlobby.service.cache;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
public class LanguageCache implements Cache<String> {

    private Map<Long, String> languageCache = new ConcurrentHashMap<>();

    @Override
    public String getData(Long userId) {
        return languageCache.get(userId);
    }

    @Override
    public String addData(Long userId, String data) {
        return languageCache.put(userId, data);
    }

    @Override
    public String removeData(Long userId) {
        return languageCache.remove(userId);
    }

    @Override
    public boolean isDataAlreadyExists(Long userId) {
        return languageCache.containsKey(userId);
    }

    @Override
    public void dropCache() {
        languageCache = new ConcurrentHashMap<>();
    }
}
