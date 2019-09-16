rootProject.name = "ktor-auth0-permissions"

val zensumPluginRev = "a7b54cc"

var localZensumPlugin = startParameter.projectProperties.containsKey("localZensumPlugin")

if (localZensumPlugin) {
    println("Using Zensum plugin from local Maven repository.")
} else {
    println("Using Zensum plugin revision $zensumPluginRev.")
}

pluginManagement {
    repositories {
        if (localZensumPlugin) {
            mavenLocal()
        } else {
            maven("https://jitpack.io")
        }

        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "se.zensum.gradle.project") {
                if (localZensumPlugin) {
                    useModule("se.zensum.gradle:zensum-gradle-plugin:1.0")
                } else {
                    useModule("com.github.zensum:zensum-gradle-plugin:${zensumPluginRev}")
                }
            }
        }
    }
}
