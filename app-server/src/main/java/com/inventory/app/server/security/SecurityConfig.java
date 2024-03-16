package com.inventory.app.server.security;

import com.inventory.app.server.error.DuplicateUserException;
import com.inventory.app.server.service.authorization.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;


@EnableWebSecurity
@Slf4j
@Configuration
public class SecurityConfig {

    //private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        try {
            return new JwtAuthenticationFilter(new JwtService(), (UserDetailsServiceImpl) userDetailsService());
        } catch (Exception e) {
            throw new RuntimeException("Error initializing JwtAuthenticationFilter", e);
        }
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    public AuthenticationManager customAuthenticationManager() throws Exception {
//        return new ProviderManager(Collections.singletonList(authenticationProvider()));
//    }

//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
//        return new JwtAuthenticationFilter(customAuthenticationManager(), new JwtService(), (UserDetailsServiceImpl) userDetailsService());
//    }


    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(this::customAuthorizeRequests)
                    .csrf(this::customCSRFProtection)
                    .formLogin(this::customFormLogin)
                    .exceptionHandling(this::customExceptionHandling)
                    .sessionManagement(this::customSessionManagement)
                    .authenticationProvider(authenticationProvider())
                    .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .logout(this::customLogout)
                    .headers(this::customHeaders)
                    .build();
    }

    private void customHeaders(HeadersConfigurer<HttpSecurity> httpSecurityHeadersConfigurer) {
        httpSecurityHeadersConfigurer
                .frameOptions(Customizer.withDefaults());
    }

    private void customLogout(LogoutConfigurer<HttpSecurity> httpSecurityLogoutConfigurer) {
        httpSecurityLogoutConfigurer
                .logoutUrl("/logout") // Set the logout URL
                .logoutSuccessUrl("/login?logout"); // Redirect to login page after logout
    }

    private void customSessionManagement(SessionManagementConfigurer<HttpSecurity> httpSecuritySessionManagementConfigurer) {
        httpSecuritySessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // stateless due to JWT tokens
    }

    private void customExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> httpSecurityExceptionHandlingConfigurer) {
        httpSecurityExceptionHandlingConfigurer
                .authenticationEntryPoint((request, response, authException) -> {
                    // Handle authentication failure
                    response.sendRedirect("/login?error=unauthorized");
                })
                .accessDeniedHandler(((request, response, acessDeniedException) -> {
                    // Handle access denied
                    response.sendRedirect("/access-denied");
                })).defaultAccessDeniedHandlerFor((request, response, exception) -> {
            // Handle default access denied
            if (exception.getCause() instanceof DuplicateUserException) {
                // Handle duplicate user exception
                response.sendRedirect("/signup?error=duplicateUser");
            } else {
                // Redirect to the login page with a generic error message for other access denied situations
                response.sendRedirect("/login?error=unauthorized");
            }
        },(RequestMatcher) request -> true);
    }


    private void customFormLogin(FormLoginConfigurer<HttpSecurity> httpSecurityFormLoginConfigurer) {
        httpSecurityFormLoginConfigurer
                .loginPage("/login") // Customize the login page URL
                .permitAll(); // Allow access to the login page without authentication
    }

    private void customCSRFProtection(CsrfConfigurer<HttpSecurity> httpSecurityCsrfConfigurer) {
        // Exclude CSRF protection for authentication endpoints
        httpSecurityCsrfConfigurer
                .ignoringRequestMatchers("/api/auth/**");
    }

    private void customAuthorizeRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // Permit access to static resources
                .requestMatchers("/api/auth/**").permitAll() // Permit access to authentication endpoints
                .requestMatchers("/signup").permitAll() // Permit access to signup page
                .requestMatchers("/admin/**").hasRole("ADMIN") // Restrict access to admin endpoints
                .requestMatchers("/user/**").hasRole("USER") // Restrict access to user endpoints
                .anyRequest().authenticated();
    }
}

