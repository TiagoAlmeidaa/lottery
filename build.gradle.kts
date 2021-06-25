plugins {
    java
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.gradle)
        classpath(Dependencies.kotlin)
        classpath(Dependencies.navigationSafeArgs)
        classpath(Dependencies.firebaseCrashlytics)
        classpath(Google.services)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.named<Delete>("clean") {
    delete(rootProject.buildDir)
}

subprojects {
    afterEvaluate {
        apply(from = "../jacoco.gradle.kts")
    }
}
