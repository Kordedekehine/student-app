package com.studentunite.studentsapp.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/validate/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/registration/**").permitAll()
                .antMatchers("/swagger-ui/**", " /v3/api-docs/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/**").permitAll()    // we want to allow retrieval of posts, comments, etc. even if not logged in
//                .antMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority(String.valueOf(AppUserRole.USER), (String.valueOf(AppUserRole.ADMIN)))
//                .antMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority(String.valueOf(AppUserRole.USER), (String.valueOf(AppUserRole.ADMIN)))
//                .antMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority((String.valueOf(AppUserRole.USER)), (String.valueOf(AppUserRole.ADMIN)))
                .anyRequest().permitAll();
//                .authenticated().and()
//                .formLogin();

        // Log configuration details for debugging
        http.authorizeRequests()
                // .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll() // Permit access to actuator endpoints
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();

        // Log CORS configuration
        http.cors(withDefaults -> log.info("CORS Configuration: {}", corsConfigurationSource()));
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            log.error("Access Denied: {}", e.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        };
    }
        @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "content-type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    }

