package org.kumoricon.registration.admin.roles;

import org.kumoricon.registration.model.role.RightRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UserRightsUpdateConfig implements WebMvcConfigurer {

    private final RightRepository rightRepository;

    public UserRightsUpdateConfig(RightRepository rightRepository) {
        this.rightRepository = rightRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserRightsUpdateInterceptor(rightRepository));
    }
}
