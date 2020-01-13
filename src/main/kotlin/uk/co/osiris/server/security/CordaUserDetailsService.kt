package uk.co.osiris.server.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

interface CordaUserDetailsService {
    @Throws(UsernameNotFoundException::class)
    fun connectToCorda(username: String, password: String, server: String, port: Int): UserDetails
}