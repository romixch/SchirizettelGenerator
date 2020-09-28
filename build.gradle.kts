
plugins {
    java
    id("application")
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
    implementation("com.itextpdf:itextpdf:5.5.13.1")
    implementation("com.opencsv:opencsv:4.6")
    implementation("org.apache.pdfbox:pdfbox:2.0.17")
    implementation("org.apache.tika:tika-parsers:1.24.1")

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
