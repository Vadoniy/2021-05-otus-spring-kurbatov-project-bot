package ru.otus.yardsportsteamlobby.service;

public interface LocalizationService {

    String getLocalizedMessage(String source);

    String getLocalizedMessage(String source, Long userId);
}