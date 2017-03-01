import com.google.protobuf.gradle.ProtobufConfigurator
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogging

fun addExt(name: String, f: () -> Any) =
    project.extensions.add(name, f())

buildscript {
    repositories {
        gradleScriptKotlin()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin"))
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.1")
    }
}

apply {
    plugin("kotlin")
    plugin("com.google.protobuf")
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile(project(":microcontrollers"))
    compile("com.google.protobuf:protobuf-java:3.2.0")
    compile("org.tinylog:tinylog:1.1")
    compile("rxbroadcast:rxbroadcast:1.1.0")
    testCompile("junit:junit:4.12")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit:1.0.6")
}

tasks.withType<Test> {
    testLogging(closureOf<TestLogging> {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    })
}

tasks["compileKotlin"].dependsOn("generateProto")

addExt("protobuf") {
    val protobuf = ProtobufConfigurator(project, null)
    protobuf.generatedFilesBaseDir = "${projectDir}/src"
    protobuf
}
