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
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile(project(":core"))
    compile(project(":microcontrollers"))
    compile("org.openjdk.jmh:jmh-core:1.18")
}

configure<ApplicationPluginConvention> {
    mainClassName = "org.openjdk.jmh.Main"
}
