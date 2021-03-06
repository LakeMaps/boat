import org.gradle.api.tasks.testing.logging.TestLogging
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    jcenter()
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.2.60"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
