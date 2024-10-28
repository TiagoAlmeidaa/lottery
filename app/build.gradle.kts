import java.util.Properties

val appId = "com.tiagoalmeida.lottery"
val appMaxSdk = 34
val appMinSdk = 22
val versionProperties: Properties = rootProject.extra.properties["versionProperties"] as Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = appId
    compileSdk = appMaxSdk

    defaultConfig {
        applicationId = appId
        minSdk = appMinSdk
        targetSdk = appMaxSdk

        versionCode = versionProperties.getProperty("versionCode").toInt()
        versionName = versionProperties.getProperty("versionName")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

    externalNativeBuild {
        cmake {
            path("CMakeLists.txt")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.appCompat)
    implementation(libs.android.ktx)
    implementation(libs.material)
    implementation(libs.lifecycle.extensions)
    implementation(libs.viewmodel.ktx)
    implementation(libs.swipeRefresh)
    implementation(libs.workManager)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.okHttpLogging)
    implementation(libs.gson)
    implementation(libs.flexbox)
    implementation(libs.coroutines)
    implementation(libs.koin)
    debugImplementation(libs.leakCanary)
    testImplementation(libs.junit)
    testImplementation(libs.coreTesting)
    testImplementation(libs.mockK)
    testImplementation(libs.coroutinesTest)
}
