package com.github.marceloleite2604.noargandsetters.properties;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Slf4j
@Validated
@ConfigurationProperties(PropertiesPath.CHAT)
@AllArgsConstructor
@Getter
public class ChatProperties {

  private final String baseUrl;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @AssertTrue(message = "base-url must be a valid URL.")
  private boolean isBaseUrlAValidUrl() {
    try {
      URI.create(baseUrl);
      return true;
    } catch (IllegalArgumentException exception) {
      log.error("Exception thrown while checking if \"{}\" is a valid URL.", baseUrl, exception);
      return false;
    }
  }
}
