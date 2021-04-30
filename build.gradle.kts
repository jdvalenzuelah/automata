import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jUnitVersion = "5.6.2"
val kgraphvizVersion = "0.18.0"
val kotlinArgParserVersion = "2.0.7"
val tinyLogVersion = "2.3.0-M1"
val kwebVersion = "0.8.6"
val kotlinpoetVersion = "1.8.0"
val cliKtVersion = "3.1.0"

plugins {
    application
    kotlin("jvm") version "1.4.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    jcenter()
}

group = "id.jasoet.boilerplate"
version = "1.0.0"

application {
    mainClassName = if(project.hasProperty("mainClass"))
        project.property("mainClass").toString()
    else
        "org.github.compiler.ui.cli.scannerGenerator.MainKt"

}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("guru.nidi:graphviz-kotlin:$kgraphvizVersion")

    implementation("com.github.ajalt.clikt:clikt:$cliKtVersion")
    implementation("com.xenomachina:kotlin-argparser:$kotlinArgParserVersion") //TODO Remove dependency

    implementation("org.tinylog:tinylog-api-kotlin:$tinyLogVersion")
    implementation("org.tinylog:tinylog-impl:$tinyLogVersion")
    implementation("org.tinylog:slf4j-tinylog:$tinyLogVersion")

    implementation("com.github.kwebio:kweb-core:$kwebVersion")

    implementation("com.squareup:kotlinpoet:$kotlinpoetVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter","spek2")
    }

    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "failed", "skipped")
    }
}

tasks.withType<KotlinCompile> {

    sourceCompatibility = "11"
    targetCompatibility = "11"

    kotlinOptions {
        jvmTarget = "11"
        apiVersion = "1.4"
        languageVersion = "1.4"
    }
}

tasks.wrapper {
    gradleVersion = "6.6.1"
}
