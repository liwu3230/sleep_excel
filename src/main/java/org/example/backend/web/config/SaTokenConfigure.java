package org.example.backend.web.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.example.backend.web.model.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @Bean
    public SaServletFilter getSaServletFilter() {
        return (new SaServletFilter())
                .addInclude("/**")
                .addExclude("/doLogin", "/login", "/static/**", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/**/*.ico")
                .setAuth(obj -> {
                    if (!StpUtil.isLogin()) {
                        SaHolder.getResponse().redirect("/login");
                        SaRouter.back(R.error("未登录！").setCode(401));
                    }
                });
    }

}
