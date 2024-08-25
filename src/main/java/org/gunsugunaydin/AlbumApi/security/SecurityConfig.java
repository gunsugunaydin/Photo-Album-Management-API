package org.gunsugunaydin.AlbumApi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private RSAKey rsaKey;

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        rsaKey = Jwks.generateRsa();
        //generate edilen RSA key'i rsaKey variable'ında tutup, proper key formatına(jwk) convert ediyoruz.
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //jwk sourse objesini alıp encoder olarak return ediyoruz.
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
         return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> 
                headers
                    .frameOptions(options -> options.sameOrigin())
            )
            .authorizeHttpRequests(requests -> 
                requests
                    .requestMatchers("/api/hello").permitAll()
                    .requestMatchers("/api/v1/auth/token").permitAll()
                    .requestMatchers("/api/v1/auth/users/add").permitAll()
                    .requestMatchers("/api/v1/auth/users/list").hasAuthority("SCOPE_ADMIN")
                    .requestMatchers("/api/v1/auth/users/{user_id}/update-authorities").hasAuthority("SCOPE_ADMIN")
                    .requestMatchers("/api/v1/auth/profile").authenticated()
                    .requestMatchers("/api/v1/auth/profile/update-password").authenticated()
                    .requestMatchers("/api/v1/auth/profile/delete").authenticated()
                    .requestMatchers("/api/v1/albums/add").authenticated()
                    .requestMatchers("/api/v1/albums/list").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/list").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/update").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/delete").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/upload-photos").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/update").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/delete").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/download-photo").authenticated()
                    .requestMatchers("/api/v1/albums/{album_id}/photos/{photo_id}/download-thumbnail").authenticated()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
            )
            .oauth2ResourceServer(oauth2 -> 
                 oauth2.jwt(Customizer.withDefaults())
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable()) 
            .headers(headers -> 
                headers
                    .frameOptions(options -> options.disable()));
                
        return http.build();
    }
}
