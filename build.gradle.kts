// zensum-gradle-plugin includes ktor, kotlin_coroutines, grpc, junit, jib
// Detail see "https://github.com/zensum/zensum-gradle-plugin/blob/master/src/main/resources/versions.properties"
// Need to specify kotlin version

plugins {
    id("org.jetbrains.kotlin.jvm") version("1.3.30")
    id("se.zensum.gradle.project")
}

group = "se.zensum"
version = "1.0-SNAPSHOT"
description = "Add support for reading permissions from Auth0 JSON web tokens in Ktor"

zensum {
    main_class = "se.zensum.MainKt"
    kotlin_version = "1.3.30"
    kotlin_api_version = "1.3"
}
