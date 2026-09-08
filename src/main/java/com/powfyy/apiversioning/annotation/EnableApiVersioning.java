package com.powfyy.apiversioning.annotation;

import com.powfyy.apiversioning.config.ApiVersionAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ApiVersionAutoConfiguration.class)
public @interface EnableApiVersioning {
}
