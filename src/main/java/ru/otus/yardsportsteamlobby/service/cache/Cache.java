package ru.otus.yardsportsteamlobby.service.cache;

import ru.otus.yardsportsteamlobby.dto.GameCreatingStateWithRequest;

public interface Cache<T> {

    T getData(Long userId);

    T addData(Long userId, T data);

    T removeData(Long userId);

    boolean isDataAlreadyExists(Long userId);
}
