package uk.co.osiris.server.security

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class CordaAuthenticationFilter : UsernamePasswordAuthenticationFilter() {
    val SPRING_SECURITY_FORM_SERVER_KEY = "server"
    val SPRING_SECURITY_FORM_PORT_KEY = "port"

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse?): Authentication {

        logger.debug("Attempt Authentication")
        // Can only login via a POST
        if (request.method != "POST") {
            throw AuthenticationServiceException("Authentication method not supported: " + request.method)
        }

        val authRequest: CordaAuthenticationToken = getAuthRequest(request)
        setDetails(request, authRequest)
        return this.authenticationManager.authenticate(authRequest)
    }

    private fun getAuthRequest(request: HttpServletRequest): CordaAuthenticationToken {
        val username = obtainUsername(request) ?: ""
        val password = obtainPassword(request) ?: ""
        val server: String = obtainServer(request)
        val port: Int = obtainPort(request)
        logger.debug("Authenticate with: $username-$password on $server:$port")
        return CordaAuthenticationToken(username, password, server, port)
    }

    private fun obtainServer(request: HttpServletRequest): String {
        return request.getParameter(SPRING_SECURITY_FORM_SERVER_KEY)
    }

    private fun obtainPort(request: HttpServletRequest): Int {
        return request.getParameter(SPRING_SECURITY_FORM_PORT_KEY).toInt()
    }
}