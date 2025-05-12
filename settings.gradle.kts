pluginManagement {
    repositories {
        google()
        mavenCentral()           // donde está LibGDX :contentReference[oaicite:0]{index=0}
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()                 // AndroidX, Play services :contentReference[oaicite:1]{index=1}
        mavenCentral()           // LibGDX y otros artefactos :contentReference[oaicite:2]{index=2}
    }
}

rootProject.name = "CercleCultural"
include(":app", ":core")
