buildscript {
    repositories {
        gradleScriptKotlin()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

apply {
    plugin("kotlin")
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile(project(":microcontrollers"))
    compile("org.tinylog:tinylog:1.1")
    compile("rxbroadcast:rxbroadcast:1.1.0")
    testCompile("junit:junit:4.12")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit:1.0.6")
}
