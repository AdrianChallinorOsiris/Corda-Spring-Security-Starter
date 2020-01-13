package uk.co.osiris.server.security

import net.corda.client.rpc.CordaRPCConnection
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CordaUser(username: String,
                password: String,
                val server: String,
                val port: Int,
                val rpcConnection: CordaRPCConnection,
                enabled: Boolean,
                accountNonExpired: Boolean,
                credentialsNonExpired: Boolean,
                accountNonLocked: Boolean,
                authorities: MutableCollection<GrantedAuthority?>?
) : User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities) {

    fun getProxy() = rpcConnection.proxy
}