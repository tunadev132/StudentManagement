package com.example.quanlysinhvien.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/auth/**").permitAll()
                .antMatchers("/student", "/student/class/**", "/course", "/class").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin();
//                .and()
//                .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                        .invalidSessionUrl("/logout?expired")
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(false))
////                .authenticationProvider(authenticationProvider)
////                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true));
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/logout")
        );
        http
                .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true));
        return http.build();
    }
}
