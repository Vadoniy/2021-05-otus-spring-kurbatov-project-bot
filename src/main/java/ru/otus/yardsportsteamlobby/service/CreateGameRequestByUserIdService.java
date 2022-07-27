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

    public CreateGameRequestByUserId saveCurrentCreateGameRequest(Long userId, CreateGameRequest createGameRequest) {
        final var createGameRequestByUserId = createGameRequestByUserIdRepository.findById(userId.toString())
                .orElse(new CreateGameRequestByUserId(userId.toString(), createGameRequest));
        createGameRequestByUserId.setCreateGameRequest(createGameRequest);
        createGameRequestByUserIdRepository.save(createGameRequestByUserId);
        log.info("CreateGameRequest for user is saved {}: {}", userId, createGameRequest);
        return createGameRequestByUserId;
    }

    public void removeCurrentCreateGameRequest(Long userId) {
        createGameRequestByUserIdRepository.deleteById(userId.toString());
        log.info("CreateGameRequest for user {} is deleted", userId);
    }
}
