plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.bytebuilders"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bytebuilders"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    // Tus dependencias, según tu version catalog
    implementation(libs.google.services)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.play.services.location)
    implementation(libs.androidx.preference.ktx)
    val room_version = "2.6.1"

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    // Firestore
    implementation(libs.firebase.firestore)

    // Livedata + coroutines
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Localización
    implementation(libs.androidx.activity)

    // KAPT para Room
    kapt("androidx.room:room-compiler:$room_version")

    // Soporte RxJava2 para Room
    implementation(libs.androidx.room.rxjava2)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(kotlin("script-runtime"))

    // Glide (carga de imagen victoria)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // Retrofit + Gson (en lugar de Moshi)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // (Opcional) OkHttp
    // implementation("com.squareup.okhttp3:okhttp:4.9.0")
    // implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Elimina Moshi si no lo usas
    // implementation("com.squareup.moshi:moshi:1.14.0")
    // implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    // kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    // Coroutines
    implementation(libs.kotlinx.coroutines.android.v172)

    // Retrofit con coroutines
    implementation(libs.adapter.rxjava2)

    // Lifecycle ViewModel + LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v260)
    implementation(libs.androidx.lifecycle.livedata.ktx)
}
