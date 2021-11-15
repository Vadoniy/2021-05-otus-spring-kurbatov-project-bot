package ru.otus.yardsportsteamlobby.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "business")
@Getter
@Setter
@Component
public class BusinessConfigurationProperties {

    private String locale;
}