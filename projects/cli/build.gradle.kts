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
    compile(project(":log"))
    compile(project(":microcontrollers"))
    compile("com.fazecast:jSerialComm:1.3.11")
    compile("org.tinylog:tinylog:1.1")
}

configure<ApplicationPluginConvention> {
    applicationName = rootProject.name
    version = rootProject.version
    mainClassName = "cli.Main"
}
