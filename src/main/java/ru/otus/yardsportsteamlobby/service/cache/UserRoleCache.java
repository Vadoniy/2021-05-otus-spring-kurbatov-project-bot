package ru.otus.yardsportsteamlobby.service.cache;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.enums.UserRole;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class UserRoleCache implements Cache<UserRole> {

    private Map<Long, UserRole> userRoleCacheMap = new HashMap<>();

    @Override
    public UserRole getData(Long userId) {
        return userRoleCacheMap.get(userId);
    }

    @Override
    public UserRole addData(Long userId, UserRole data) {
        return userRoleCacheMap.put(userId, data);
    }

    @Override
    public UserRole removeData(Long userId) {
        return userRoleCacheMap.remove(userId);
    }

    @Override
    public boolean isDataAlreadyExists(Long userId) {
        return userRoleCacheMap.containsKey(userId);
    }

    @Override
    public void dropCache() {
        userRoleCacheMap = new HashMap<>();
    }
}
