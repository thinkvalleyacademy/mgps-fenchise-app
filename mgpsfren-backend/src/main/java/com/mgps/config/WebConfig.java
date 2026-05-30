package com.mgps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // Additional web configuration can be added here
    // For now, CORS is handled in CorsConfig class
}
