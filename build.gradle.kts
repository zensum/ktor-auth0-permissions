plugins {
    id("org.jetbrains.kotlin.jvm") version("1.3.50")
    id("application") apply true
}

application {
    mainClassName = "se.zensum.creditors.MainKt"
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
    implementation("io.ktor", "ktor-server-netty", ktor)
    implementation("io.ktor", "ktor-auth-jwt", ktor)
}