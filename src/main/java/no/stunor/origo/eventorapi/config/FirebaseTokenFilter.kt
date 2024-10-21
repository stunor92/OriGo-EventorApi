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
        val path = request.requestURI
        if (path.startsWith("/rest/api-docs") || path.startsWith("/rest/webjars/") ||
            path.startsWith("/rest/swagger-ui/")) {
            chain.doFilter(request, response)
            return
        }
        try {
            val authorizationHeader = request.getHeader("Authorization")
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header!")
                throw UnauthorizedException("Missing or invalid Authorization header!")
            }

            val token = authorizationHeader.removePrefix("Bearer ")

            val decodedToken: FirebaseToken = try {
                FirebaseAuth.getInstance().verifyIdToken(token)
            } catch (e: FirebaseAuthException) {
                logger.warn("Error! $e")
                throw UnauthorizedException("Error! $e")
            }

            val uid = decodedToken.uid
            request.setAttribute("uid", uid)

            chain.doFilter(request, response)
        } catch (ex: UnauthorizedException) {
            logger.warn("Unauthorized request: ${ex.message}")
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.writer.write(ex.message ?: "Unauthorized")
        }
    }
}