package ru.otus.yardsportsteamlobby.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.otus.yardsportsteamlobby.repository.redis.SignUpForGameByUserId;

public interface SignUpForGameByUserIdRepository extends KeyValueRepository<SignUpForGameByUserId, String> {
}
