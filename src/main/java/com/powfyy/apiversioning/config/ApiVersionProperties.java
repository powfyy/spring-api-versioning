package com.powfyy.apiversioning.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api.versioning")
public class ApiVersionProperties {
    private String externalPrefix = "/api";
    private String internalPrefix = "/internal/api";
    private String pathSuffix;
}
