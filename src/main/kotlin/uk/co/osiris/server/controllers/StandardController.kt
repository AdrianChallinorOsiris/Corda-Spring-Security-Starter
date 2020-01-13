package uk.co.osiris.server.controllers

import uk.co.osiris.server.security.CordaUser
import net.corda.core.contracts.ContractState
import net.corda.core.messaging.vaultQueryBy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * A CorDapp-agnostic controller that exposes standard endpoints.
 */
@Controller
@RequestMapping("/") // The paths for GET and POST requests are relative to this base path.
class StandardController {


    fun proxy(upat: UsernamePasswordAuthenticationToken) = (upat.principal as CordaUser).getProxy()


    @GetMapping(value = ["/status"])
    fun status(m: MutableMap<String, Any>, u: UsernamePasswordAuthenticationToken): String {
        m["servertime"] = LocalDateTime.ofInstant(proxy(u).currentNodeTime(), ZoneId.of("UTC")).toString()
        m["addresses"] = proxy(u).nodeInfo().addresses.toString()
        m["identities"] = proxy(u).nodeInfo().legalIdentities.toString()
        m["version"] = proxy(u).nodeInfo().platformVersion.toString()
        val x = proxy(u).networkMapSnapshot().flatMap { it.legalIdentities }.toSet()
        val y = x.map{ it.name.toString() }
        m["peers"] = y
        m["notaries"] = proxy(u).notaryIdentities().toString()
        m["flows"] = proxy(u).registeredFlows()
        m["states"] = proxy(u).vaultQueryBy<ContractState>().states.toString()
        return "status"
    }

    @GetMapping(value = ["/servertime"], produces = ["text/plain"])
    private fun serverTime(u: UsernamePasswordAuthenticationToken) = LocalDateTime.ofInstant(proxy(u).currentNodeTime(), ZoneId.of("UTC")).toString()

    @GetMapping(value = ["/addresses"], produces = ["text/plain"])
    private fun addresses(u: UsernamePasswordAuthenticationToken) = proxy(u).nodeInfo().addresses.toString()

    @GetMapping(value = ["/identities"], produces = ["text/plain"])
    private fun identities(u: UsernamePasswordAuthenticationToken) = proxy(u).nodeInfo().legalIdentities.toString()

    @GetMapping(value = ["/platformversion"], produces = ["text/plain"])
    private fun platformVersion(u: UsernamePasswordAuthenticationToken) = proxy(u).nodeInfo().platformVersion.toString()

    @GetMapping(value = ["/peers"], produces = ["text/plain"])
    private fun peers(u: UsernamePasswordAuthenticationToken) = proxy(u).networkMapSnapshot().flatMap { it.legalIdentities }.toString()

    @GetMapping(value = ["/notaries"], produces = ["text/plain"])
    private fun notaries(u: UsernamePasswordAuthenticationToken) = proxy(u).notaryIdentities().toString()

    @GetMapping(value = ["/flows"], produces = ["text/plain"])
    private fun flows(u: UsernamePasswordAuthenticationToken) = proxy(u).registeredFlows().toString()

    @GetMapping(value = ["/states"], produces = ["text/plain"])
    private fun states(u: UsernamePasswordAuthenticationToken) = proxy(u).vaultQueryBy<ContractState>().states

}