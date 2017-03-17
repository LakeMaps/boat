buildscript {
    repositories {
        gradleScriptKotlin()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

plugins {
    application
}

apply {
    plugin("kotlin")
    plugin("kotlin-kapt")
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile(project(":core"))
    compile(project(":microcontrollers"))
    compile("org.openjdk.jmh:jmh-core:1.18")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:1.18")
}

configure<ApplicationPluginConvention> {
    mainClassName = "org.openjdk.jmh.Main"
}
