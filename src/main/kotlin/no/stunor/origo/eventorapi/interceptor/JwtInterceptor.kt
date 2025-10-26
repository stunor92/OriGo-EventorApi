package no.stunor.origo.eventorapi.interceptor

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
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
            val secretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
            val claimsJws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            val uid = claimsJws.payload["sub"]?.toString() ?: ""
            request.setAttribute("uid", uid)
        }
        return true
    }
}