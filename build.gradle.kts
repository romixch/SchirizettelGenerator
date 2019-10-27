
plugins {
    java
    id("application")
    id("com.palantir.graal") version "0.6.0-14-g6fa0c0a"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

buildscript {
    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.eclipse.birt.runtime:org.eclipse.birt.runtime:4.3.1") {
        exclude("org.milyn", "flute")
    }
    implementation("com.opencsv:opencsv:4.6")

    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest:2.1")
}

application {
    mainClassName = "ch.romix.schirizettel.generator.GeneratorGUI"
}

// This only works for Linux. It creates an executable binary file.
graal {
    outputName("schirizettelGenerator")
    mainClass("ch.romix.schirizettel.generator.GeneratorGUI")
}
