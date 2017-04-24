import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogging

repositories {
    jcenter()
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.1.2"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    compile(project(":log"))
    testCompile("junit:junit:4.12")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<Test> {
    testLogging(closureOf<TestLogging> {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    })
}
