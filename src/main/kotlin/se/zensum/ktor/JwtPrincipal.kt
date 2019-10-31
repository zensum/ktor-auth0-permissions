package se.zensum.ktor

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.auth.Principal

data class JwtPrincipal(
    private val claims: Map<String, Claim>
): Principal, JsonWebToken, Map<String, Claim> by claims {
    val permissions: List<String> = permissions(claims)

    override fun audience(): List<String> = audience(claims)

    fun hasPermission(permission: String): Boolean = permission in permissions

    companion object {
        fun fromPayload(payload: Payload): JwtPrincipal =
            JwtPrincipal(payload.claims)

        fun fromMap(map: Map<String, Any>): JwtPrincipal {
            val claims: Map<String, Claim> = map.asSequence()
                .map { it.key to jacksonObjectMapper().convertValue<JsonNode>(it.value) }
                .map { it.first to JsonNodeClaim(it.second) }
                .toMap()

            return JwtPrincipal(claims)
        }

        private fun permissions(claims: Map<String, Claim>): List<String> {
            return when {
                "permissions" in claims -> claims.resolveClaim("permissions") {
                    it.asList(String::class.java)
                }
                "scope" in claims -> claims.resolveClaim("scope") {
                    it.asString().split(Regex("\\s+"))
                }
                else -> emptyList()
            }
        }

        private fun Map<String, Claim>.resolveClaim(claimKey: String, resolve: (Claim) -> List<String>): List<String> {
            val claim: Claim? = this[claimKey]
            return when {
                claim == null -> emptyList()
                claim.isNull -> emptyList()
                else -> resolve(claim)
            }
        }

        private fun audience(claims: Map<String, Claim>): List<String> {
            val audience: Claim = claims["aud"] ?: return emptyList()
            val audienceAsList: List<String>? = audience.asList(String::class.java)
            val audienceAsString: String? = audience.asString()
            return when {
                !audienceAsList.isNullOrEmpty() -> audienceAsList
                audienceAsString != null -> listOf(audienceAsString)
                else -> emptyList()
            }
        }
    }
}