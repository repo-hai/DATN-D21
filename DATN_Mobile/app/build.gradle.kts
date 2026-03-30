plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.datn_mobile"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.datn_mobile"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

// KSP Configuration - Disable incremental processing to fix FileAlreadyExistsException
ksp {
    arg("incremental", "false")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- Networking ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // --- JSON parsing ---
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.kotlinx.serialization)
    // ------- data store ---------
    implementation(libs.datastore.preferences)
    //--------- hilt for jetpack compose --------
    implementation(libs.hilt.navigation.compose)
    // -------------navigation for jetpack---------------
    implementation(libs.navigation.compose)

    implementation(libs.coil.compose)

    // Import Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Import Firebase Messaging
    implementation(libs.firebase.messaging)

    // Import thư viện hỗ trợ await() cho Task
    implementation(libs.kotlinx.coroutines.play.services)
}