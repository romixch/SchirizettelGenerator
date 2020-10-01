
plugins {
    java
    id("application")
    id("com.hendraanggrian.packr") version ("0.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
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

packr {

    executable = "Schirizettelgenerator"
    classpath("build/libs/SchirizettelGenerator.jar")
    verbose = true
    outputDirectory = "./build/packr"
    mainClass = "ch.romix.schirizettel.generator.GeneratorGUI"

    linux64()
}

tasks.named("packLinux64") {
    dependsOn(":jar")
}