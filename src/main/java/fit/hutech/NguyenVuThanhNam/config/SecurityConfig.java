package fit.hutech.NguyenVuThanhNam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring()
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**");
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/register", "/error").permitAll()
                                                .requestMatchers("/api/**").permitAll()
                                                .requestMatchers("/books").permitAll()
                                                // Admin dashboard - chi ADMIN truy cap
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/books/add", "/books/edit/**", "/books/delete/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/categories/**").hasRole("ADMIN")
                                                // Payment & Orders & Notifications - yeu cau dang nhap
                                                .requestMatchers("/payment/**").authenticated()
                                                .requestMatchers("/orders/**").authenticated()
                                                .requestMatchers("/notifications/**").authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**", "/notifications/**"));

                return http.build();
        }
}

