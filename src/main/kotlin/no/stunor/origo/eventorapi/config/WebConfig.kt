package no.stunor.origo.eventorapi.config

import no.stunor.origo.eventorapi.interceptor.JwtInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var jwtInterceptor: JwtInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor)
    }
}