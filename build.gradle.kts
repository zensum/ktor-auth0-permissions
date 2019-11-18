plugins {
    id("org.jetbrains.kotlin.jvm") version("1.3.50")
    id("java-library") apply true
    id("maven") apply true
}

group = "se.zensum"
version = "0.1.0"
description = "Add support for reading permissions from Auth0 JSON web tokens in Ktor"

defaultTasks = mutableListOf("test")

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/ktor")
}

dependencies {
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8")

    // Logging
    implementation("io.github.microutils", "kotlin-logging", "1.6.20")

    api("com.typesafe", "config", "1.3.4")

    // Ktor
    val ktor = "1.2.0"
    implementation(platform("io.ktor:ktor-server-core:$ktor"))
    api("io.ktor", "ktor-auth-jwt", ktor)

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.10")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.10")
    implementation("com.fasterxml.jackson.datatype:jackson-modules-java8:2.9.10")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

tasks {
    test {
        useJUnitPlatform()

        // Show test results.
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
        reports {
            junitXml.isEnabled = false
            html.isEnabled = true
        }
    }
}