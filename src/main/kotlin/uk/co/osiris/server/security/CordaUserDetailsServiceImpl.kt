package uk.co.osiris.server.security

import net.corda.client.rpc.CordaRPCClient
import net.corda.core.utilities.NetworkHostAndPort
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service("userDetailsService")
class CordaUserDetailsServiceImpl : CordaUserDetailsService {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Throws(AuthenticationException::class)
    override fun connectToCorda(username: String, password: String, server: String, port: Int): UserDetails {
        if (StringUtils.isAnyBlank(username, server)) {
            throw  UsernameNotFoundException("Username and server must be provided")
        }

        try {
            // Try to connect to corda
            logger.info("Connecting....")
            val rpcAddress = NetworkHostAndPort(server, port)
            val rpcClient = CordaRPCClient(rpcAddress)
            val rpcConnection = rpcClient.start(username, password)
            logger.info("Connected as $username to $rpcAddress")

            // Now create the user to pass back
            val authority = SimpleGrantedAuthority("CORDA")
            val authorities: MutableCollection<GrantedAuthority?>? = arrayListOf(authority)
            return CordaUser(username, password, server, port, rpcConnection, true,
                    true, true, true, authorities)
        } catch (e: Exception) {
            throw  UsernameNotFoundException(
                    String.format("Username not found for server, username=%s, server=%s:%d", username, server, port))
        }
    }

}