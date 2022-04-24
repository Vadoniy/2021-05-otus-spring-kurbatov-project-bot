package ru.otus.yardsportsteamlobby.service;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import ru.otus.yardsportsteamlobby.configuration.properties.BusinessConfigurationProperties;
import ru.otus.yardsportsteamlobby.service.cache.LanguageCache;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LocalizationServiceImpl extends
        ResourceBundleMessageSource implements LocalizationService {

    private final BusinessConfigurationProperties businessConfigurationProperties;

    private final MessageSource messageSource;

    private final LanguageCache languageCache;

    public LocalizationServiceImpl(BusinessConfigurationProperties businessConfigurationProperties, MessageSource messageSource, LanguageCache languageCache) {
        this.businessConfigurationProperties = businessConfigurationProperties;
        this.messageSource = messageSource;
        this.languageCache = languageCache;
        setDefaultEncoding("UTF-8");
    }

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

    @Override
    public Map<String, String> getLocalizedMessages(String messagePath, Long userId) {
        final var locale = Optional.ofNullable(languageCache.getData(userId))
                .orElse(businessConfigurationProperties.getLocale());
        return Optional.ofNullable(getResourceBundle("i18n/messages", Locale.forLanguageTag(locale)))
                .map(ResourceBundle::keySet)
                .orElse(Set.of()).stream()
                .filter(buttonNamePath -> buttonNamePath.startsWith(messagePath))
                .collect(Collectors.toMap(buttonNamePath -> buttonNamePath, buttonNamePath -> getLocalizedMessage(buttonNamePath, userId)));
    }

    @Override
    public String getCurrentLocale(Long userId) {
        return Optional.ofNullable(languageCache.getData(userId))
                .orElse(businessConfigurationProperties.getLocale());
    }
}