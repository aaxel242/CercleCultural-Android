plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.cercleculturalandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cercleculturalandroid"
        minSdk = 24
        targetSdk = 35
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
        viewBinding = true
    }

}

dependencies {

    implementation (libs.retrofit)
    //noinspection UseTomlInstead
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation (libs.lottie)
    implementation (libs.androidx.core.ktx)
    implementation (libs.androidx.appcompat)
    implementation (libs.material)
    implementation (libs.androidx.activity)
    implementation (libs.androidx.constraintlayout)

    // libGDX core y backend Android
    implementation (libs.gdx)
    implementation (libs.gdx.backend.android)
    implementation (libs.gdx.box2d)

    // Sólo los natives soportados por Android (no natives-armeabi)
    implementation (libs.com.badlogicgames.gdx.gdx.box2d.platform)
    implementation (libs.com.badlogicgames.gdx.gdx.box2d.platform)
    implementation (libs.com.badlogicgames.gdx.gdx.box2d.platform)
    implementation (libs.com.badlogicgames.gdx.gdx.box2d.platform)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}