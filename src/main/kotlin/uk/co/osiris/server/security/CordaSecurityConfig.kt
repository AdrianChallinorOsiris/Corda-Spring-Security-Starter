package uk.co.osiris.server.security

import CordaUserDetailsAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
open class CordaSecurityConfig(private val cordaUserDetailsService: CordaUserDetailsService) : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)


    override fun configure(http: HttpSecurity) {
        http
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers("/css/**", "/index", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())

    }

    private fun authenticationFilter(): CordaAuthenticationFilter {
        val filter = CordaAuthenticationFilter()

        filter.setAuthenticationManager(authenticationManagerBean())
        filter.setAuthenticationFailureHandler(failureHandler())
        return filter
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) = auth.authenticationProvider(authProvider())

    fun authProvider() = CordaUserDetailsAuthenticationProvider(passwordEncoder(), cordaUserDetailsService)

    fun failureHandler() = SimpleUrlAuthenticationFailureHandler("/login?error=true")

    /**
     * We KNOW ths is not secure, but we need the password in plain so we can later pass it to CORDA.
     */
    fun passwordEncoder() = NoOpPasswordEncoder.getInstance()
}