object Plugin {
    const val ANDROID_APPLICATION = "com.android.application"
    const val KOTLIN_ANDROID = "kotlin-android"
    const val KOTLIN_KAPT = "kotlin-kapt"
    const val GOOGLE_SERVICES = "com.google.gms.google-services"
    const val JACOCO = "jacoco"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase.crashlytics"
    const val SAFE_ARGS = "androidx.navigation.safeargs.kotlin"
}

object AndroidConfig {
    const val APPLICATION_ID = "com.tiagoalmeida.lottery"
    const val COMPILE_SDK_VERSION = 30
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 30
    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0.0"
    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
    const val CMAKE_PATH = "CMakeLists.txt"
}

object BuildType {
    const val RELEASE = "release"
    const val DEBUG = "debug"
}

object ProGuard {
    const val TXT = "proguard-android.txt"
    const val PRO = "proguard-rules.pro"
}