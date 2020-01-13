package uk.co.osiris.server.security

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Bean
fun logoutSuccessHandler(): LogoutSuccessHandler? {
    return CordaLogoutHandler()
}

class CordaLogoutHandler : SimpleUrlLogoutSuccessHandler(), LogoutSuccessHandler {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Throws(IOException::class, ServletException::class)
    override fun onLogoutSuccess(
            request: HttpServletRequest,
            response: HttpServletResponse?,
            authentication: Authentication?) {
        val cordauser: CordaUser = authentication!!.principal as CordaUser
        logger.info("Disconnecting ")
        cordauser.rpcConnection.notifyServerAndClose()
        super.onLogoutSuccess(request, response, authentication)
    }
}