allprojects {
    repositories {
        gradleScriptKotlin()
    }
}

allprojects {
    task("resolveAllDependencies", {
        doLast { configurations.all { it.resolve() } }
    })
}

plugins {
    base
}
