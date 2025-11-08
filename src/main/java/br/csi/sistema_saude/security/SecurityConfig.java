package br.csi.sistema_saude.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AutenticacaoFilter autenticacaoFilter;

    public SecurityConfig(AutenticacaoFilter filtro) {
        this.autenticacaoFilter = filtro;
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {


        //libera os endpoint
        //desabilitar quando for para produção
        http
                .csrf(crsf -> crsf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CONFIGURA CORS
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:4200")); // URL do front
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    corsConfig.setAllowCredentials(true); // permite envio de cookies/autenticação
                    return corsConfig;
                }))

                .authorizeHttpRequests(auth -> auth

                       // libera Swagger e documentação
                                .requestMatchers(
                                        "/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/swagger-resources/**",
                                        "/webjars/**"

                                ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()

                                .requestMatchers(HttpMethod.POST, "/usuario/salvar").permitAll()
                                .requestMatchers(HttpMethod.POST, "/farmacia/salvar").permitAll()
                                .requestMatchers(HttpMethod.POST, "/farmacia/salvar").permitAll()

                                // Endpoint restrito a ADMIN
                                .requestMatchers("/usuario/listar-usuarios").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/banco-medicamentos/salvar").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/banco-medicamentos/atualizar").hasAuthority("ROLE_ADMIN")
                                 .requestMatchers("/banco-medicamentos/deletar/{id}").hasAuthority("ROLE_ADMIN")


                                // Endpoint /recolhimento → somente ROLE_FARMACIA
                                .requestMatchers("/recolhimento/**").hasAuthority("ROLE_FARMACIA")

                                .requestMatchers(HttpMethod.GET, "/farmacia/listar-farmacias").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/farmacia/atualizar-farmacia").hasAuthority("ROLE_FARMACIA")

                                // Qualquer outro endpoint exige autenticação
                                .anyRequest().authenticated()

                )
                .addFilterBefore(this.autenticacaoFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

}
