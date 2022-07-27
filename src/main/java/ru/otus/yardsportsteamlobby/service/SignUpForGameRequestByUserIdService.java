package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.dto.SignUpForGameRequest;
import ru.otus.yardsportsteamlobby.repository.SignUpForGameByUserIdRepository;
import ru.otus.yardsportsteamlobby.repository.redis.SignUpForGameByUserId;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpForGameRequestByUserIdService {

    private final SignUpForGameByUserIdRepository signUpForGameByUserIdRepository;

    public Optional<SignUpForGameByUserId> getSignUpForGameRequest(Long userId) {
        return signUpForGameByUserIdRepository.findById(userId.toString());
    }

    public SignUpForGameByUserId saveSignUpForGameRequest(Long userId, SignUpForGameRequest signUpForGameRequest) {
       final var signUpForGameByUserId = signUpForGameByUserIdRepository.findById(userId.toString())
                .orElse(new SignUpForGameByUserId(userId.toString(), signUpForGameRequest));
        signUpForGameByUserId.setSignUpForGameRequest(signUpForGameRequest);
        signUpForGameByUserIdRepository.save(signUpForGameByUserId);
        log.info("CreateGameRequest for user is saved {}: {}", userId, signUpForGameRequest);
        return signUpForGameByUserId;
    }

    public void removeSignUpForGameRequest(Long userId) {
        signUpForGameByUserIdRepository.deleteById(userId.toString());
        log.info("CreateGameRequest for user {} is deleted", userId);
    }
}
