plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // API multiplataforma de LibGDX
    implementation("com.badlogicgames.gdx:gdx:1.11.0")                // :contentReference[oaicite:3]{index=3}
    // (Opcional) Si usas Box2D en core:
    // implementation("com.badlogicgames.gdx:gdx-box2d:1.11.0")       :contentReference[oaicite:4]{index=4}
}
