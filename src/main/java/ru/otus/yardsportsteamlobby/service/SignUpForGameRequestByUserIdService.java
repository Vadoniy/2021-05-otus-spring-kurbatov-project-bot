package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.SignUpForGameRequest;
import ru.otus.yardsportsteamlobby.repository.SignUpForGameByUserIdRepository;
import ru.otus.yardsportsteamlobby.repository.redis.SignUpForGameByUserId;
import ru.otus.yardsportsteamlobby.service.cache.SignUpForGameCache;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpForGameRequestByUserIdService extends SignUpForGameCache {

    private final SignUpForGameByUserIdRepository signUpForGameByUserIdRepository;

    public Optional<SignUpForGameByUserId> getSignUpForGameRequest(Long userId) {
        return signUpForGameByUserIdRepository.findById(userId.toString());
    }

    public void saveSignUpForGameRequest(Long userId, SignUpForGameRequest signUpForGameRequest) {
        signUpForGameByUserIdRepository.findById(userId.toString())
                .ifPresentOrElse(createGameRequestByUserId ->
                                createGameRequestByUserId.setSignUpForGameRequest(signUpForGameRequest),
                        () -> new SignUpForGameByUserId(userId.toString(), signUpForGameRequest));
        log.info("CreateGameRequest for user is saved {}: {}", userId, signUpForGameRequest);
    }

    public void removeSignUpForGameRequest(Long userId) {
        signUpForGameByUserIdRepository.deleteById(userId.toString());
        log.info("CreateGameRequest for user {} is deleted", userId);
    }
}
