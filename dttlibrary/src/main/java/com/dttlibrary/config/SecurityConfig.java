package com.dttlibrary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Các trang công khai
                .requestMatchers("/", "/login", "/register", "/books/**").permitAll()
                // Các trang yêu cầu vai trò cụ thể
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**", "/borrowings/**").hasAnyRole("MEMBER", "LIBRARIAN")
                // Bất kỳ request nào khác đều cần xác thực
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customLoginSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Bỏ qua hoàn toàn việc kiểm tra bảo mật cho các tài nguyên tĩnh để tối ưu hiệu suất
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/favicon.ico");
    }
}
