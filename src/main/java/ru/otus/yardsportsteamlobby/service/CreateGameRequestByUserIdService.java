package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.CreateGameRequest;
import ru.otus.yardsportsteamlobby.repository.CreateGameRequestByUserIdRepository;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateGameRequestByUserIdService {

    private final CreateGameRequestByUserIdRepository createGameRequestByUserIdRepository;

    public Optional<CreateGameRequestByUserId> getCurrentCreateGameRequest(Long userId) {
        return createGameRequestByUserIdRepository.findById(userId.toString());
    }

    public void saveCurrentCreateGameRequest(Long userId, CreateGameRequest createGameRequest) {
        createGameRequestByUserIdRepository.findById(userId.toString())
                .ifPresentOrElse(createGameRequestByUserId ->
                                createGameRequestByUserId.setCreateGameRequest(createGameRequest),
                        () -> new CreateGameRequestByUserId(userId.toString(), createGameRequest));
        log.info("CreateGameRequest for user is saved {}: {}", userId, createGameRequest);
    }

    public void removeCurrentCreateGameRequest(Long userId) {
        createGameRequestByUserIdRepository.deleteById(userId.toString());
        log.info("CreateGameRequest for user {} is deleted", userId);
    }
}
