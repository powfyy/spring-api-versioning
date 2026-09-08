package com.powfyy.apiversioning.impl;

import com.powfyy.apiversioning.config.ApiVersionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RequiredArgsConstructor
public class ApiVersionWebMvcRegistrations implements WebMvcRegistrations {

  private final ApiVersionProperties apiVersionProperties;

  @Override
  public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
    return new VersionedRequestMappingHandlerMapping(apiVersionProperties);
  }
}
