// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Estos alias vienen de tu libs.versions.toml, se aplican "apply false" porque
    // se usan en los sub-proyectos (módulos).
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false

    // Plugins de Google y Crashlytics con versión declarada
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Suele ser recomendable tener repositorios en settings.gradle.kts o en buildscript {}
// Pero si necesitas, podrías agregar algo como:
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    // Normalmente no hace falta dependencies aquí, si usas versionCatalog y aliases
}
