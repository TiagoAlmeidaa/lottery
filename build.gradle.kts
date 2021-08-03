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
        classpath("com.android.tools.build:gradle:7.0.0")
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
