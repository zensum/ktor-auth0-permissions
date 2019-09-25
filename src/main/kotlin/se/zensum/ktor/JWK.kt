package se.zensum.ktor

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.jwt
import se.zensum.Config
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

fun Authentication.Configuration.auth0(
    name: String? = null,
    jwkIssuer: String = getConfig("jwk.issuer"),
    jwkRealm: String = getConfig("jwk.realm"),
    jwkAudience: String = getConfig("jwk.audience")
) {
    val jwkProvider: JwkProvider = JwkProviderBuilder(jwkIssuer)
        .cached(5, 30, TimeUnit.MINUTES)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    jwt(name) {
        verifier(jwkProvider, jwkIssuer)
        realm = jwkRealm
        validate { credentials: JWTCredential ->
            val principal: JwtPrincipal = JwtPrincipal.fromPayload(credentials.payload)
            if(jwkAudience in principal.audience) {
                principal
            } else {
                null
            }
        }
    }
}

/**
 * Check if a configuration value is set in application.conf, or similar file.
 * If a configuration key is not present, fallback to environment variable for
 * same key, but with usual convention for environment variables. So config key
 * `jwk.issuer` becomes `JWK_ISSUER`.
 */
private fun getConfig(configKey: String): String {
    val envVarKey: String = configKey.toUpperCase().replace(".", "_")
    val envVarValue: String? = System.getenv(envVarKey)
    val configValue: String? = Config[configKey]
    return configValue ?: envVarValue
    ?: throw IllegalStateException("No value found for config key '$configKey' or environment variable '$envVarValue'")
}