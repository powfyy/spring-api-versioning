package com.powfyy.apiversioning.config;

import com.powfyy.apiversioning.impl.ApiVersionWebMvcRegistrations;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApiVersionProperties.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ApiVersionAutoConfiguration {

  @Bean
  public ApiVersionWebMvcRegistrations apiVersionWebMvcRegistrations(ApiVersionProperties apiVersionProperties) {
    return new ApiVersionWebMvcRegistrations(apiVersionProperties);
  }
}
