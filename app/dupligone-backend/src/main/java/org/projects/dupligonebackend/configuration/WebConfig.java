package org.projects.dupligonebackend.configuration;

import org.projects.dupligonebackend.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private SessionInterceptor sessionInterceptor;

    public WebConfig(SessionInterceptor sessionInterceptor){
        this.sessionInterceptor = sessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/api/**") // apply to all API routes
                .excludePathPatterns("/api/session/start"); // don't require on session start
    }
}
