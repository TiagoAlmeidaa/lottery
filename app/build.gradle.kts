plugins {
    id(Plugin.ANDROID_APPLICATION)
    id(Plugin.KOTLIN_ANDROID)
    id(Plugin.KOTLIN_KAPT)
    id(Plugin.GOOGLE_SERVICES)
    id(Plugin.JACOCO)
    id(Plugin.FIREBASE_CRASHLYTICS)
    id(Plugin.SAFE_ARGS)
}

jacoco {
    toolVersion = "0.8.7"
}

android {
    compileSdkVersion(AndroidConfig.COMPILE_SDK_VERSION)
    defaultConfig {
        applicationId(AndroidConfig.APPLICATION_ID)
        minSdkVersion(AndroidConfig.MIN_SDK_VERSION)
        targetSdkVersion(AndroidConfig.TARGET_SDK_VERSION)

        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME
        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
    }
    buildTypes {
        getByName(BuildType.DEBUG) {
            isTestCoverageEnabled = true
        }
        getByName(BuildType.RELEASE) {
            isMinifyEnabled = false
            proguardFiles(ProGuard.TXT, ProGuard.PRO)
        }
    }
    buildFeatures {
        dataBinding = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    externalNativeBuild {
        cmake {
            path(AndroidConfig.CMAKE_PATH)
        }
    }
}

dependencies {
    implementation(Jetbrains.kotlin)

    implementation(Android.appCompat)
    implementation(Android.coreKtx)
    implementation(Android.material)
    implementation(Android.constraintLayout)
    implementation(Android.lifecycleExtensions)
    implementation(Android.viewModelKtx)
    implementation(Android.swipeRefreshLayout)
    implementation(Android.paging)
    implementation(Android.workManager)

    implementation(Navigation.ui)
    implementation(Navigation.ktx)

    kapt(Android.dataBinding)

    implementation(Firebase.core)
    implementation(Firebase.analytics)
    implementation(Firebase.crashlytics)

    implementation(Retrofit.core)
    implementation(Retrofit.converter)
    implementation(Retrofit.logging)

    implementation(Google.gson)
    implementation(Google.flexBox)

    implementation(Coroutines.android)

    implementation(Koin.android)

    implementation(ViewBinding.propertyDelegate)

    testImplementation(Tests.jUnit)
    testImplementation(Tests.coreTesting)
    testImplementation(Tests.mockK)
    testImplementation(Tests.coroutines)
    androidTestImplementation(Tests.runner)
    androidTestImplementation(Tests.espresso)
}