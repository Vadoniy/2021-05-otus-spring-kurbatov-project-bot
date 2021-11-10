package ru.otus.yardsportsteamlobby.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "telegrambot")
@Component
@Getter
@Setter
public class TelegramBotConfigurationProperties {

    private String userName;

    private String botToken;

    private String webhookPath;
}
