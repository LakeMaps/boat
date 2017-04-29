repositories {
    jcenter()
}

plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.1.2"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    compile(project(":core"))
    compile(project(":gps"))
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
