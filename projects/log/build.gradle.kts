import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    jcenter()
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.1.3"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
