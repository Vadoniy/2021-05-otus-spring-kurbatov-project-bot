package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;
import ru.otus.yardsportsteamlobby.repository.CreatePlayerRequestByUserIdRepository;
import ru.otus.yardsportsteamlobby.repository.redis.CreatePlayerRequestByUserId;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePlayerRequestByUserIdService {

    private final CreatePlayerRequestByUserIdRepository createPlayerRequestByUserIdRepository;

    public Optional<CreatePlayerRequestByUserId> getCurrentCreatePlayerRequest(Long userId) {
        return createPlayerRequestByUserIdRepository.findById(userId.toString());
    }

    public CreatePlayerRequestByUserId saveCurrentCreateGameRequest(Long userId, CreatePlayerRequest createPlayerRequest) {
        final var createPlayerRequestByUserId = createPlayerRequestByUserIdRepository.findById(userId.toString())
                .orElse(new CreatePlayerRequestByUserId().setUserId(userId.toString()));
        createPlayerRequestByUserId.setCreatePlayerRequest(createPlayerRequest);
        log.info("CreatePlayerRequest for user {}: {}", userId, createPlayerRequest);
        return createPlayerRequestByUserIdRepository.save(createPlayerRequestByUserId);
    }

    public boolean isDataAlreadyExists(Long userId) {
        return createPlayerRequestByUserIdRepository.existsById(userId.toString());
    }
}
