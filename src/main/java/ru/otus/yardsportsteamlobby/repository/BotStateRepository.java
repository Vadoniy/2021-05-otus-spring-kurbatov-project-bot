package ru.otus.yardsportsteamlobby.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.otus.yardsportsteamlobby.repository.redis.BotStateForCurrentUser;

public interface BotStateRepository extends KeyValueRepository<BotStateForCurrentUser, String> {
}
