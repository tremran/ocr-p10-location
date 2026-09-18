package com.example.chatpoc.realtime;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure.web-pubsub")
public record AzureWebPubSubProperties(String connectionString, String hubName) {
}