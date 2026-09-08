package com.powfyy.apiversioning.utils;

import com.powfyy.apiversioning.config.ApiVersionProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ApiVersioningHelper {

  private final ApiVersionProperties properties;

  private static final Pattern VERSION_AT_START_PATTERN = Pattern.compile("^/v(\\d+)(/|$)");

  /**
   * Версия API текущего запроса, определенная по пути (/api/v1/..., /internal/api/v2/...).
   * Возвращает null, если версия не определена в пути или properties не инициализированы.
   */
  public Integer currentVersion() {
    if (properties == null) {
      return null;
    }

    HttpServletRequest request =
        ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

    String path = request.getRequestURI();
    String contextPath = request.getContextPath();
    if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
      path = path.substring(contextPath.length());
    }

    String matchedPrefix = resolveMatchingPrefix(path);
    if (matchedPrefix == null) {
      return null;
    }

    String remainder = path.substring(matchedPrefix.length());
    Matcher matcher = VERSION_AT_START_PATTERN.matcher(remainder);
    if (matcher.find()) {
      return Integer.valueOf(matcher.group(1));
    }
    return null;
  }

  private String resolveMatchingPrefix(String path) {
    String internalPrefix = properties.getInternalPrefix();
    String externalPrefix = properties.getExternalPrefix();

    if (internalPrefix != null && !internalPrefix.isBlank() && path.startsWith(internalPrefix)) {
      return internalPrefix;
    }
    if (externalPrefix != null && !externalPrefix.isBlank() && path.startsWith(externalPrefix)) {
      return externalPrefix;
    }
    return null;
  }
}