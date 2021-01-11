package com.example.demo.config;

import com.example.demo.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//添加@EnableGlobalMethodSecurity注解开启Spring方法级安全
// prePostEnabled 决定Spring Security的前注解是否可用 [@PreAuthorize,@PostAuthorize,..],设置为true
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtFilter jwtFilter;

    public WebSecurityConfig(CustomLogoutSuccessHandler customLogoutSuccessHandler, CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, JwtFilter jwtFilter) {
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                //登录后,访问没有权限处理类
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                //匿名访问,没有权限的处理类
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                //退出登录
                .and()
                .logout().logoutUrl("/logout").logoutSuccessHandler(customLogoutSuccessHandler)

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()

                //无状态登录，取消session管理
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                //登录请求过滤
                .addFilterBefore(jwtLoginFilter(),UsernamePasswordAuthenticationFilter.class)
                //验证authorization过滤
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtLoginFilter jwtLoginFilter() throws Exception {
        return new JwtLoginFilter("/login",authenticationManager());
    }

    /**
     * 指定加密方式
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
