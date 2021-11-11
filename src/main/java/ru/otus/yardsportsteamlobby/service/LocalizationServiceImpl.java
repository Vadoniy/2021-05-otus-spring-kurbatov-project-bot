package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.configuration.properties.BusinessConfigurationProperties;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LocalizationServiceImpl implements LocalizationService {

    private final BusinessConfigurationProperties businessConfigurationProperties;

    private final MessageSource messageSource;

    @Override
    public String getLocalizedMessage(String source) {
        return messageSource.getMessage(source, new Object[]{}, Locale.forLanguageTag(businessConfigurationProperties.getLocale()));
    }
}