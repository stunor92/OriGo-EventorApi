package no.stunor.origo.eventorapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
open class SecurityConfig(
    private val firebaseTokenFilter: FirebaseTokenFilter
) {

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf {} // Enable CSRF protection
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .anyRequest().permitAll() // All other endpoints
            }
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic {} // Basic authentication

        return http.build()
    }
}