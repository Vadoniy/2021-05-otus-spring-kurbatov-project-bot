package ru.otus.yardsportsteamlobby.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.otus.yardsportsteamlobby.repository.redis.DeletePlayerRequestByUserId;

public interface DeletePlayerRequestByUserIdRepository extends KeyValueRepository<DeletePlayerRequestByUserId, String> {
}
