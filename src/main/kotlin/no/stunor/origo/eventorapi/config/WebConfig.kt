package no.stunor.origo.eventorapi.config

import no.stunor.origo.eventorapi.interceptor.JwtInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebConfig(
    @Autowired private val jwtInterceptor: JwtInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor)
    }
}