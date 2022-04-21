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

    public void saveCurrentCreateGameRequest(Long userId, CreatePlayerRequest createPlayerRequest) {
        createPlayerRequestByUserIdRepository.findById(userId.toString())
                .ifPresentOrElse(createPlayerRequestByUserId ->
                                createPlayerRequestByUserId.setCreatePlayerRequest(createPlayerRequest),
                        () -> new CreatePlayerRequestByUserId(userId.toString(), createPlayerRequest));
        log.info("CreatePlayerRequest for user {}: {}", userId, createPlayerRequest);
    }

    public boolean isDataAlreadyExists(Long userId) {
        return createPlayerRequestByUserIdRepository.existsById(userId.toString());
    }
}
