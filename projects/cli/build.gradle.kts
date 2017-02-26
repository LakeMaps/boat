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
    compile(project(":core"))
    compile(project(":microcontrollers"))
    compile(kotlinModule("stdlib"))
    compile("com.fazecast:jSerialComm:1.3.11")
    compile("org.tinylog:tinylog:1.1")
}

configure<ApplicationPluginConvention> {
    mainClassName = "cli.Main"
}
