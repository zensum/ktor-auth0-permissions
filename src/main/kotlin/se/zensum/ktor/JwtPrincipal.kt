package se.zensum.ktor

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import io.ktor.auth.Principal

data class JwtPrincipal(
    val subject: String,
    val audience: List<String>,
    val permissions: List<String>
): Principal {
    fun hasPermission(permission: String): Boolean = permission in permissions

    companion object {
        fun fromPayload(payload: Payload): JwtPrincipal {
            val permissions: List<String> = when {
                "permissions" in payload.claims -> payload.resolveClaim("permissions") {
                    it.asList(String::class.java)
                }
                "scope" in payload.claims -> payload.resolveClaim("scope") {
                    it.asString().split(Regex("\\s+"))
                }
                else -> emptyList()
            }
            require(payload.subject != null) { "No subject present in token" }
            val subject: String = payload.subject!!
            val audience: List<String> = audience(payload)
            return JwtPrincipal(subject, audience, permissions)
        }

        private fun Payload.resolveClaim(claimKey: String, resolve: (Claim) -> List<String>): List<String> {
            val claim: Claim? = this.getClaim(claimKey)
            return when {
                claim == null -> emptyList()
                claim.isNull -> emptyList()
                else -> resolve(claim)
            }
        }

        private fun audience(token: Payload): List<String> {
            val audience: Claim = token.getClaim("aud")
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