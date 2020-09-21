package com.newgrand.superviseanalysis.config;

import com.newgrand.superviseanalysis.filter.GlobalInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class FilterConfig implements WebMvcConfigurer {
    @Resource
    private GlobalInterceptor globalInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(globalInterceptor);
        // 配置全部拦截
        registration.addPathPatterns("/**");

        // 开放请求
        registration.excludePathPatterns(
                "/**/*.html",  // html静态资源
                "/**/*.js",    // js静态资源
                "/**/*.css",   // css静态资源
                "/images/**",   // 图片
                "/lib/**",
                "/data/**",
                "/editor-app/**",
                "/model/**"

        );
    }
}
