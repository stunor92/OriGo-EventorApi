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
class FirebaseTokenFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            val token = request.getHeader("Authorization") ?: throw UnauthorizedException("Missing token!")

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
        } catch (ex: Exception) {
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.writer.write("Internal Server Error")
        }
    }
}