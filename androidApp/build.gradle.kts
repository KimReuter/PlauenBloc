plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
    alias(libs.plugins.jetbrains.kotlin.serialization)

}

android {
    namespace = "com.example.plauenblod.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.plauenblod.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // Lottie Animation
    implementation(libs.lottie.compose)


    // Firestore
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.app)

    // Datum
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.kotlinx.datetime)

    // Async Image
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Constraint Layout
    implementation(libs.androidx.constraintlayout.compose)

}