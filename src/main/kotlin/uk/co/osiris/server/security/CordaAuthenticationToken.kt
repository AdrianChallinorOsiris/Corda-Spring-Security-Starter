package uk.co.osiris.server.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class CordaAuthenticationToken : UsernamePasswordAuthenticationToken {
    var server: String
        private set
    var port: Int
        private set

    constructor(principal: Any?, credentials: Any?, server: String, port: Int) : super(principal, credentials) {
        this.server = server
        this.port = port
        super.setAuthenticated(false)
    }

    constructor(principal: Any?, credentials: Any?, server: String, port: Int,
                authorities: Collection<GrantedAuthority?>?) : super(principal, credentials, authorities) {
        this.server = server
        this.port = port
        super.setAuthenticated(true) // must use super, as we override
    }

}