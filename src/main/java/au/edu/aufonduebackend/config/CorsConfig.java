package au.edu.aufonduebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow specific origins
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "https://au-fondue-web.vercel.app",
            "http://localhost:3000",
            "https://aufondue-backend.kindisland-399ef298.southeastasia.azurecontainerapps.io"
        ));
        
        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow all headers, including our custom ones
        config.setAllowedHeaders(Arrays.asList(
            "Content-Type", 
            "Authorization", 
            "X-User-Type", 
            "X-User-Id",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Expose headers so the client can read them
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "X-User-Type",
            "X-User-Id",
            "Content-Type"
        ));
        
        // Set max age for preflight requests
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}