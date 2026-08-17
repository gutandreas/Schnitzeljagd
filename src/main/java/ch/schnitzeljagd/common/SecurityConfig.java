package ch.schnitzeljagd.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Absicherung des Adminbereichs.
 * <p>
 * Bewusst schlicht: ein einziges Konto, dessen Passwort aus der Umgebung kommt
 * ({@code SCHNITZELJAGD_ADMIN_PASSWORD}). Alles andere ist öffentlich — die
 * Teilnehmenden sollen ohne Anmeldung scannen und antworten können.
 * <p>
 * Wichtig gegenüber der alten Version: Die Prüfung passiert im Server, nicht
 * im Browser. Früher lag das Passwort im ausgelieferten JavaScript und war in
 * Sekunden rückrechenbar; die Admin-Endpunkte selbst waren völlig ungeschützt.
 */
@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${schnitzeljagd.admin-password}")
    private String adminPassword;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        if ("admin".equals(adminPassword)) {
            log.warn("Es wird das Standard-Adminpasswort verwendet. Auf dem Server unbedingt "
                    + "SCHNITZELJAGD_ADMIN_PASSWORD setzen!");
        }
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll());
        return http.build();
    }
}
