package se.zensum.ktor

import com.auth0.jwt.interfaces.Claim
import java.time.Instant

interface JsonWebToken: Map<String, Claim> {
    /**
     * @return the issuer (`iss`) of the token
     */
    fun issuer(): String = getValue("iss").asString()

    /**
     * @return the subject (`sub`) for the token
     */
    fun subject(): String = getValue("sub").asString()

    /**
     * @return the audience(s) (`aud`) for the token
     */
    fun audience(): List<String>

    /**
     * @return the expiry time (`exp`) of the token
     */
    fun expiresAt(): Instant = claimAsInstant("exp")

    /**
     * @return the time from which the token is valid (`nbf`). A token
     * cannot be used before this point in time.
     */
    fun notBefore(): Instant? = claimAsInstant("nbf")

    /**
     * @return the time for when the token was issued (`iat`)
     */
    fun issuedAt(): Instant = claimAsInstant("iat")

    private fun claimAsInstant(claim: String): Instant {
        val unixEpochSeconds: Long = getValue(claim).asLong()
        return Instant.ofEpochSecond(unixEpochSeconds)
    }

    /**
     * @return the id for the token (`jti`), if present, else null
     */
    fun id(): String? = this["jti"]?.asString()
}