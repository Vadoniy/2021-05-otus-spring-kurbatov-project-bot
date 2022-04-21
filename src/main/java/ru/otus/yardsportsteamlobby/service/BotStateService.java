package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.repository.BotStateRepository;
import ru.otus.yardsportsteamlobby.repository.redis.BotStateForCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotStateService {

    private final BotStateRepository botStateRepository;

    public boolean isDataAlreadyExists(Long userId) {
        return botStateRepository.existsById(userId.toString());
    }

    public BotStateForCurrentUser getCurrentBotState(Long userId) {
        return botStateRepository.findById(userId.toString()).orElse(null);
    }

    public void saveBotStateForUser(Long userId, BotState botState) {
        botStateRepository.findById(userId.toString())
                .ifPresentOrElse(botStateForCurrentUser ->
                                botStateForCurrentUser.setBotState(botState),
                        () -> new BotStateForCurrentUser(userId.toString(), botState));
        log.info("New state {} for user {}", botState, userId);
    }
}
