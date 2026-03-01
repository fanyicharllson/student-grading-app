plugins {
    kotlin("jvm") version "2.2.0"
}

group = "com.charlseempire"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    //Newly added dependencies for Apache POI
    implementation("org.apache.poi:poi-ooxml:5.4.0")
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

// Package everything into one runnable (fat) JAR
tasks.jar {
    manifest { attributes["Main-Class"] = "com.charlseempire.MainKt" }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
