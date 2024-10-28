import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.navigation.safeargs) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

val versionProperties by extra { getCustomProperties("version.properties") }

fun getCustomProperties(filePath: String): Properties {
    val customProperties = File(filePath)
    val properties = Properties()

    if (customProperties.canRead()) {
        customProperties.inputStream().use {
            properties.load(it)
        }
    }

    return properties
}
