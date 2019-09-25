package se.zensum

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config as TypeSafeConfig

internal object Config: Map<String, String> by loadProperties()

private fun loadProperties(): Map<String, String> {
    val env: String? = System.getenv("APP_ENV")
    val config = ConfigFactory.load().forEnvironment(env)
    return config.entrySet().asSequence()
        .map { it.key to it.value.unwrapped().toString() }
        .toMap()
}

private fun TypeSafeConfig.forEnvironment(environment: String?): TypeSafeConfig {
    val envKeyPrefix: String = environment?.toLowerCase() ?: return this
    return if(hasPath(envKeyPrefix)) {
        this
            .getConfig(environment.toLowerCase())
            .withFallback(this)
    } else {
        this
    }
}