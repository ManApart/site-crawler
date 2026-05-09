
plugins {
    kotlin("jvm") version "2.3.20"
}

group = "org.rak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://github.com/psiegman/mvn-repo/raw/master/releases")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
    implementation("org.jsoup:jsoup:1.20.1")
    implementation("nl.siegmann.epublib:epublib-core:3.1") {
        exclude("org.sl4j")
        exclude("xmlpull")
    }
    testImplementation("junit", "junit", "4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}
