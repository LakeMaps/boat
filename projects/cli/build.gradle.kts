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
    compile(kotlinModule("stdlib"))
}

configure<ApplicationPluginConvention> {
    mainClassName = "cli.Main"
}
