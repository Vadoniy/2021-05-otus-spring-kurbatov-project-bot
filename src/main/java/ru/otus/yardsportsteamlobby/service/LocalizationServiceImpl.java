package ru.otus.yardsportsteamlobby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.configuration.properties.BusinessConfigurationProperties;
import ru.otus.yardsportsteamlobby.service.cache.LanguageCache;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalizationServiceImpl implements LocalizationService {

    private final BusinessConfigurationProperties businessConfigurationProperties;

    private final MessageSource messageSource;

    private final LanguageCache languageCache;

    @Override
    public String getLocalizedMessage(String source) {
        return messageSource.getMessage(source, new Object[]{}, Locale.forLanguageTag(businessConfigurationProperties.getLocale()));
    }

    @Override
    public String getLocalizedMessage(String source, Long userId) {
        final var locale = Optional.ofNullable(languageCache.getData(userId))
                .orElse(businessConfigurationProperties.getLocale());
        return messageSource.getMessage(source, new Object[]{}, Locale.forLanguageTag(locale));
    }
}