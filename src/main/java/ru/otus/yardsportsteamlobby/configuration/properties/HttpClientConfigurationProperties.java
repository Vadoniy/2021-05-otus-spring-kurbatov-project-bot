package ru.otus.yardsportsteamlobby.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "client.rest-api")
public class HttpClientConfigurationProperties {

    private String baseUrl;
}
