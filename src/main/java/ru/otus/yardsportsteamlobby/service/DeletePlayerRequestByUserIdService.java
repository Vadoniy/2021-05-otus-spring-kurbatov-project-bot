package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.repository.DeletePlayerRequestByUserIdRepository;
import ru.otus.yardsportsteamlobby.repository.redis.DeletePlayerRequestByUserId;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletePlayerRequestByUserIdService {

    private DeletePlayerRequestByUserIdRepository deletePlayerRequestByUserIdRepository;

    public void removeDeletePlayerIdRequest(Long userId) {
        deletePlayerRequestByUserIdRepository.deleteById(userId.toString());
        log.info("Player {} is deleted", userId);
    }

    public void saveDeletePlayerIdRequest(Long userId) {
        deletePlayerRequestByUserIdRepository.save(new DeletePlayerRequestByUserId(userId.toString()));
        log.info("Delete player request for userId {} is saved", userId);
    }
}
