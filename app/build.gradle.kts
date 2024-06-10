plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.clockinapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clockinapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
        buildFeatures{
            viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.core:core-ktx:1.13.1")

    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation ("com.google.android.material:material:1.2.0")
    implementation ("com.google.android.material:material:1.3.0-alpha03")
    implementation ("com.google.firebase:firebase-auth:21.0.1")

    implementation("com.google.gms:google-services:4.4.2")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")

    implementation("com.github.Philjay:MPAndroidChart:v3.1.0")
    implementation("com.google.firebase:firebase-database:20.1.0")

}