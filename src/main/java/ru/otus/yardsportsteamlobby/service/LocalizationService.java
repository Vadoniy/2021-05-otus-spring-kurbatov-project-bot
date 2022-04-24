package ru.otus.yardsportsteamlobby.service;

import java.util.Map;

public interface LocalizationService {

    String getLocalizedMessage(String source);

    String getLocalizedMessage(String source, Long userId);

    Map<String, String> getLocalizedMessages(String messagePath, Long userId);

    String getCurrentLocale(Long userId);
}