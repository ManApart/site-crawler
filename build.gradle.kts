
plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    application
}

group = "org.rak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://github.com/psiegman/mvn-repo/raw/master/releases")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jsoup:jsoup:1.20.1")
    implementation("nl.siegmann.epublib:epublib-core:3.1") {
        exclude("org.sl4j")
        exclude("xmlpull")
    }
    testImplementation("junit", "junit", "4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}
