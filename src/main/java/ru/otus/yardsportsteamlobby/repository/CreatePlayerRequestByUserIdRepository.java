package ru.otus.yardsportsteamlobby.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.otus.yardsportsteamlobby.repository.redis.CreatePlayerRequestByUserId;

public interface CreatePlayerRequestByUserIdRepository extends KeyValueRepository<CreatePlayerRequestByUserId, String> {
}
