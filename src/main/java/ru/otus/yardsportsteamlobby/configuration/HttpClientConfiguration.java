package ru.otus.yardsportsteamlobby.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.otus.yardsportsteamlobby.configuration.properties.HttpClientConfigurationProperties;

@Configuration
public class HttpClientConfiguration {

    @Bean
    public RestTemplate apiRestTemplate(RestTemplateBuilder restTemplateBuilder, HttpClientConfigurationProperties httpClientConfigurationProperties) {
        return restTemplateBuilder.rootUri(httpClientConfigurationProperties.getBaseUrl()).build();
    }

}
