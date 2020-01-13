import uk.co.osiris.server.security.CordaAuthenticationToken
import uk.co.osiris.server.security.CordaUserDetailsService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.Assert

class CordaUserDetailsAuthenticationProvider(
        val passwordEncoder: PasswordEncoder,
        private val userDetailsService: CordaUserDetailsService
) : AbstractUserDetailsAuthenticationProvider() {

    /**
     * The password used to perform
     * [PasswordEncoder.matches] on when the user is
     * not found to avoid SEC-2056. This is necessary, because some
     * [PasswordEncoder] implementations will short circuit if the password is not
     * in a valid format.
     */
    private var userNotFoundEncodedPassword: String? = null

    @Throws(AuthenticationException::class)
    override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) {
        if (authentication.credentials == null) {
            logger.debug("Authentication failed: no credentials provided")
            throw BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"))
        }
        val presentedPassword = authentication.credentials
                .toString()
        if (!passwordEncoder.matches(presentedPassword, userDetails.password)) {
            logger.debug("Authentication failed: password does not match stored value")
            throw BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"))
        }
    }

    @Throws(Exception::class)
    override fun doAfterPropertiesSet() {
        Assert.notNull(userDetailsService, "A UserDetailsService must be set")
        userNotFoundEncodedPassword = passwordEncoder.encode(USER_NOT_FOUND_PASSWORD)
    }

    @Throws(AuthenticationException::class)
    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails {
        logger.debug("Getting authentication: $authentication")
        val auth: CordaAuthenticationToken = authentication as CordaAuthenticationToken
        return userDetailsService.connectToCorda(auth.principal.toString(), auth.credentials.toString(), auth.server, auth.port)
    }

    companion object {
        /**
         * The plaintext password used to perform
         * PasswordEncoder#matches(CharSequence, String)}  on when the user is
         * not found to avoid SEC-2056.
         */
        private const val USER_NOT_FOUND_PASSWORD = "userNotFoundPassword"
    }

}