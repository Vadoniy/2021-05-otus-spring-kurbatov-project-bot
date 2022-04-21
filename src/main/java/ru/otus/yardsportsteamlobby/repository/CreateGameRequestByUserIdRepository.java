package ru.otus.yardsportsteamlobby.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.otus.yardsportsteamlobby.repository.redis.CreateGameRequestByUserId;

public interface CreateGameRequestByUserIdRepository extends KeyValueRepository<CreateGameRequestByUserId, String> {
}
