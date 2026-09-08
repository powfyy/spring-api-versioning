package com.powfyy.apiversioning.condition;

import com.powfyy.apiversioning.config.ApiVersionProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.condition.RequestCondition;


@Getter
@RequiredArgsConstructor
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    /**
     * Текущая версия запроса
     */
    private final Integer apiVersion;

    /**
     * Свойства версионирования
     */
    private final ApiVersionProperties apiVersionProperties;

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        return new ApiVersionRequestCondition(other.getApiVersion(), other.getApiVersionProperties());
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        return this;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest exchange) {
        return other.getApiVersion().compareTo(getApiVersion());
    }


    @Override
    public String toString() {
        return "@ApiVersion(" + apiVersion + ")";
    }
}
