package com.example.chatpoc.realtime;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AzureWebPubSubProperties.class)
public class AzureWebPubSubConfiguration {
}