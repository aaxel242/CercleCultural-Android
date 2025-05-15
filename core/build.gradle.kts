// Remove redundant pluginManagement/dependencyResolutionManagement
// Keep only:
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

val gdxVersion = "1.12.0"
dependencies {
    api("com.badlogicgames.gdx:gdx:$gdxVersion")
}