import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    jcenter()
}

plugins {
    id("org.jetbrains.kotlin.jvm")  version "1.1.2"
    id("org.jetbrains.kotlin.kapt") version "1.1.2"
    application
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    compile(project(":core"))
    compile(project(":microcontrollers"))
    compile("org.openjdk.jmh:jmh-core:1.18")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:1.18")
}

configure<ApplicationPluginConvention> {
    mainClassName = "org.openjdk.jmh.Main"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
