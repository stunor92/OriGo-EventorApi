package no.stunor.origo.eventorapi.interceptor

import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor : HandlerInterceptor {

    @Value("\${config.jwt.secret}")
    private lateinit var jwtSecret: String

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.removePrefix("Bearer ")
            val claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body
            val uid = claims["sub"] as String
            request.setAttribute("uid", uid)
        }
        return true
    }
}