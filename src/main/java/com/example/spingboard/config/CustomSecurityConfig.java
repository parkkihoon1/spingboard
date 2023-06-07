package com.example.spingboard.config;

import com.example.spingboard.security.CustomUserDetailService;
import com.example.spingboard.security.handler.Custom403Handler;
import com.example.spingboard.security.handler.CustomSocialLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
    //주입 필요
    private final DataSource dataSource;
    private final CustomUserDetailService userDetailService;

    //로그인 하지 않고 볼 수 있도록 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("----------configure-----------");

        //커스텀 로그인 페이지
        http.formLogin().loginPage("/member/login");

        // CSRF 토큰 비활성화
        http.csrf().disable();

        //자동로그인을 위함
        http.rememberMe()
                .key("12345678")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailService)
                .tokenValiditySeconds(60*60*24*30);
        //Custom403Handler 화면 출력
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());

        //소셜로그인(카카오)
        http.oauth2Login().loginPage("/member/login").successHandler(authenticationSuccessHandler());
        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){ //Custom403Handler 화면 출력
        return new Custom403Handler();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        log.info("------------web configure-----------");

        return (web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
}
