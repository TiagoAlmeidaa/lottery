object Dependencies {
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsGradle}"
}

object Jetbrains {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
}

object Android {
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleExtensions}"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModelKtx}"
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"
    const val workManager = "androidx.work:work-runtime-ktx:${Versions.workManager}"
}

object Navigation {
    const val ui = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val ktx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
}

object Google {
    const val services = "com.google.gms:google-services:${Versions.googlePlayServices}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val flexBox = "com.google.android.flexbox:flexbox:${Versions.flexBox}"
}

object Firebase {
    const val analytics = "com.google.firebase:firebase-analytics:${Versions.analytics}"
    const val crashlytics = "com.google.firebase:firebase-crashlytics:${Versions.crashlytics}"
}

object Retrofit {
    const val core = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val converter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.logging}"
}

object Tests {
    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val coreTesting = "androidx.arch.core:core-testing:${Versions.coreTesting}"
    const val mockK = "io.mockk:mockk:${Versions.mockK}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val runner = "androidx.test:runner:${Versions.runner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}

object Coroutines {
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
}

object Koin {
    const val android = "io.insert-koin:koin-android:${Versions.koin}"
}

object LeakCanary {
    const val core = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
}