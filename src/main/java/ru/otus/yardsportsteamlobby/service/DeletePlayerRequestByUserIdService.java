package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.repository.DeletePlayerRequestByUserIdRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletePlayerRequestByUserIdService {

    private DeletePlayerRequestByUserIdRepository deletePlayerRequestByUserIdRepository;

    public void removeDeletePlayerIdRequest(Long userId) {
        deletePlayerRequestByUserIdRepository.deleteById(userId.toString());
        log.info("Player {} is deleted", userId);
    }
}
