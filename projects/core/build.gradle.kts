import com.google.protobuf.gradle.ProtobufConfigurator
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogging
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

project.extensions.add("protobuf", ProtobufConfigurator(project, null))

repositories {
    jcenter()
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.1.2"
    id("com.google.protobuf") version "0.8.1"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    compile(project(":units"))
    compile(project(":gps"))
    compile(project(":microcontrollers"))
    compile("com.google.protobuf:protobuf-java:3.2.0")
    compile("org.tinylog:tinylog:1.1")
    compile("rxbroadcast:rxbroadcast:1.1.0")
    testCompile("junit:junit:4.12")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<Test> {
    testLogging(closureOf<TestLogging> {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    })
}

tasks.withType<KotlinCompile> {
    dependsOn.add("generateProto")
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

configure<ProtobufConfigurator> {
    generatedFilesBaseDir = "$projectDir/src"
}
