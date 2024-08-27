package com.nmy.autologging.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "auto-tren-loger")
public class AppConfig {
    boolean isConsoleLog;
    boolean isDatabaseLog;
    String logLevel;

}
