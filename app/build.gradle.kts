plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.cercleculturalandroid"
    compileSdk = 35

    /*sourceSets {
        main {
            jniLibs.srcDirs = ['src/main/jniLibs']
        }
    }*/

    defaultConfig {
        applicationId = "com.example.cercleculturalandroid"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += setOf("arm64-v8a", "armeabi-v7a") // Orden importante
        }
    }

    packagingOptions {

        pickFirsts += setOf(
            "**/libgdx.so",
            "**/libgdx-*.so"
                               )
            excludes += setOf(
                "META-INF/robovm/ios/robovm.xml",
                "lib/armeabi/**"
                             )

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

val gdxVersion = "1.12.0" // Match app module


dependencies {
    implementation ("com.badlogicgames.gdx:gdx:1.12.0")
    implementation ("com.badlogicgames.gdx:gdx-backend-android:1.12.0")

    runtimeOnly ("com.badlogicgames.gdx:gdx-platform:1.12.0:natives-arm64-v8a")
    runtimeOnly ("com.badlogicgames.gdx:gdx-platform:1.12.0:natives-armeabi-v7a")

    // Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // UI
    implementation(libs.androidx.fragment.ktx.v162)
    implementation(libs.lottie)
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Project
    implementation(project(":core"))
}