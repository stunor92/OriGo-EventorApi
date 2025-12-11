package no.stunor.origo.eventorapi.config

import no.stunor.origo.eventorapi.interceptor.JwtInterceptor
import no.stunor.origo.eventorapi.interceptor.OptionalJwtInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebConfig(
    @param:Autowired private val jwtInterceptor: JwtInterceptor,
    @param:Autowired private val optionalJwtInterceptor: OptionalJwtInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        // Optional JWT for event-list endpoints (allows anonymous access)
        registry.addInterceptor(optionalJwtInterceptor)
            .addPathPatterns("/event-list/**")
            .excludePathPatterns("/actuator/**", "/api-docs/**", "/documentation.html", "/swagger-ui/**")

        // Required JWT for person and user endpoints
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/person/**", "/user/**")
            .excludePathPatterns("/actuator/**", "/api-docs/**", "/documentation.html", "/swagger-ui/**")
    }
}