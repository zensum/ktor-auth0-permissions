package se.zensum.ktor

import com.auth0.jwt.interfaces.Claim
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Instant

class JwtPrincipalTest {

    @Test
    fun testCreatingJwtPrincipalFromMap() {
        val claims: Map<String, Any> = mapOf(
            "aud" to "my-audience",
            "iss" to "https://my-tenant.auth0.com/",
            "sub" to "subject",
            "iat" to 1572361316,
            "exp" to 1572369316
        )
        val principal = JwtPrincipal.fromMap(claims)

        assertEquals(listOf("my-audience"), principal.audience())
        assertEquals("https://my-tenant.auth0.com/", principal.issuer())
        assertEquals("subject", principal.subject())
        assertEquals(Instant.ofEpochSecond(1572361316), principal.issuedAt())
        assertEquals(Instant.ofEpochSecond(1572369316), principal.expiresAt())
    }

    @Test
    fun testCreatingJwtPrincipalFromMapWithMultipleAudiences() {
        val claims: Map<String, Any> = mapOf(
            "aud" to listOf("audience-0", "audience-1")
        )
        val principal = JwtPrincipal.fromMap(claims)

        assertEquals(2, principal.audience().size)
        assertEquals(listOf("audience-0", "audience-1"), principal.audience())
    }

    @Test
    fun testPermissionsInJwtPrincipalWithPermissions() {
        val claims: Map<String, Any> = mapOf(
            "permissions" to listOf("pms0", "pms1", "pms3")
        )

        val principal = JwtPrincipal.fromMap(claims)

        assertEquals(3, principal.permissions.size)
        assertEquals(listOf("pms0", "pms1", "pms3"), principal.permissions)
    }

    @Test
    fun testPermissionsInJwtPrincipalWithScope() {
        val claims: Map<String, Any> = mapOf(
            "scope" to "pms0 pms1 pms3"
        )

        val principal = JwtPrincipal.fromMap(claims)

        assertEquals(3, principal.permissions.size)
        assertEquals(listOf("pms0", "pms1", "pms3"), principal.permissions)
    }

    @Test
    fun testAsArrayOnJwtPrincipal() {
        val claims: Map<String, Any> = mapOf(
            "items" to listOf(-1L, 0L, 1L)
        )

        val principal = JwtPrincipal.fromMap(claims)
        val values: Array<Long>? = principal.getValue("items").asArray(Long::class.java)

        assertNotNull(values)
        assertEquals(3, values!!.size)
        assertEquals(-1L, values[0])
        assertEquals(0L, values[1])
        assertEquals(1L, values[2])
    }
}