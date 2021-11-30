package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.client.YardSportsTeamLobbyClient;
import ru.otus.yardsportsteamlobby.enums.UserRole;
import ru.otus.yardsportsteamlobby.service.cache.UserRoleCache;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

    private final UserRoleCache userRoleCache;

    private final YardSportsTeamLobbyClient yardSportsTeamLobbyClient;

    public String getUserRoleByUserId(Long userId) {
        if (!userRoleCache.isDataAlreadyExists(userId)) {
            Optional.ofNullable(yardSportsTeamLobbyClient.getUsersRoleByUserId(userId))
                    .map(ResponseEntity::getBody)
                    .ifPresentOrElse(ur -> userRoleCache.addData(userId, UserRole.valueOf(ur)),
                            () -> userRoleCache.addData(userId, UserRole.NEW));
        }
        return userRoleCache.getData(userId).name();
    }

    public void updateUsersRole(Long userId, String userRole) {
        userRoleCache.addData(userId, UserRole.valueOf(userRole));
    }

    public void deleteUserRole(Long userId) {
        userRoleCache.removeData(userId);
    }
}
