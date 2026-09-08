package com.powfyy.apiversioning.impl;

import com.powfyy.apiversioning.annotation.ApiVersion;
import com.powfyy.apiversioning.condition.ApiVersionRequestCondition;
import com.powfyy.apiversioning.config.ApiVersionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class VersionedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {


    private final ApiVersionProperties apiVersionProperties;

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return createRequestCondition(handlerType);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return createRequestCondition(method);
    }

    private RequestCondition<ApiVersionRequestCondition> createRequestCondition(AnnotatedElement target) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(target, ApiVersion.class);
        if (apiVersion == null) {
            return null;
        }
        return new ApiVersionRequestCondition(apiVersion.value(), apiVersionProperties);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = this.buildFluxRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = this.buildFluxRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }

            ApiVersion typeApiVersion = AnnotationUtils.getAnnotation(handlerType, ApiVersion.class);
            ApiVersion methodApiVersion = AnnotationUtils.getAnnotation(method, ApiVersion.class);
            if (typeApiVersion != null || methodApiVersion != null) {
                int version = methodApiVersion != null ? methodApiVersion.value() : typeApiVersion.value();
                boolean internal = (typeApiVersion != null && typeApiVersion.internal())
                                   || (methodApiVersion != null && methodApiVersion.internal());


                String basePrefix = internal ? apiVersionProperties.getInternalPrefix() : apiVersionProperties.getExternalPrefix();

                String prefix = basePrefix + "/v" + version;
                if (apiVersionProperties.getPathSuffix() != null && !apiVersionProperties.getPathSuffix().isBlank()) {
                    prefix = prefix + apiVersionProperties.getPathSuffix().trim();
                }
                info = RequestMappingInfo.paths(prefix).build().combine(info);
            }
        }

        return info;
    }

    private RequestMappingInfo buildFluxRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class) element) : this.getCustomMethodCondition((Method) element);
        return requestMapping != null ? this.createRequestMappingInfo(requestMapping, condition) : null;
    }
}
