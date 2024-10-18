package no.stunor.origo.eventorapi.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.stunor.origo.eventorapi.exception.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
open class FirebaseTokenFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            val authorizationHeader = request.getHeader("Authorization")
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw UnauthorizedException("Missing or invalid Authorization header!")
            }

            val token = authorizationHeader.removePrefix("Bearer ")

            val decodedToken: FirebaseToken = try {
                FirebaseAuth.getInstance().verifyIdToken(token)
            } catch (e: FirebaseAuthException) {
                throw UnauthorizedException("Error! $e")
            }

            val uid = decodedToken.uid
            request.setAttribute("uid", uid)

            chain.doFilter(request, response)
        } catch (ex: UnauthorizedException) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.writer.write(ex.message ?: "Unauthorized")
        }
    }
}