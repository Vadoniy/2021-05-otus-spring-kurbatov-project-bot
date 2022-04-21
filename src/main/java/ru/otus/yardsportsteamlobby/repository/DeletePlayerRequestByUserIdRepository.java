package ru.otus.yardsportsteamlobby.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface DeletePlayerRequestByUserIdRepository extends KeyValueRepository<Object, String> {
}
