
plugins {
    java
    id("application")
    id("com.palantir.graal") version "0.6.0-14-g6fa0c0a"
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
        exclude("org.eclipse.birt.runtim", "org.eclipse.orbit.mongodb")
    }
    implementation("com.itextpdf:itextpdf:5.5.13.1")
    implementation("com.opencsv:opencsv:4.6")

    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest:2.1")
}

application {
    mainClassName = "ch.romix.schirizettel.generator.GeneratorGUI"
}

tasks.withType<CreateStartScripts>(CreateStartScripts::class.java) {
    doLast {
        var text = windowsScript.readText()
        text = text.replaceFirst(Regex("(set CLASSPATH=%APP_HOME%\\\\lib\\\\).*"), "set CLASSPATH=%APP_HOME%\\\\lib\\\\*")
        windowsScript.writeText(text)
    }
}

// This only works for Linux. It creates an executable binary file.
graal {
    outputName("schirizettelGenerator")
    mainClass("ch.romix.schirizettel.generator.GeneratorGUI")
}
