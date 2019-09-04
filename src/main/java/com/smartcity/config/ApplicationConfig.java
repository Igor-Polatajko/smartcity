/*
 * Copyright Homeaway, Inc 2019-Present. All Rights Reserved.
 * No unauthorized use of this software.
 */
package com.smartcity.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Import({DBConfig.class, EmailConfig.class, WebSecurityConfig.class, SwaggerConfig.class,
        MethodSecurityConfig.class, WebSocketConfig.class})
@Configuration
@ComponentScan(basePackages = "com.smartcity")
public class ApplicationConfig extends AbstractSecurityWebApplicationInitializer {
}
