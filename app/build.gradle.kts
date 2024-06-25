plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdkVersion(33)
    defaultConfig {
        applicationId = "com.example.MIONE_APP"
        minSdkVersion(26)
        targetSdkVersion(31)
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // Especificar el namespace aquí
    namespace = "com.example.MIONE_APP"

    // Configuración para resolver el problema con los archivos de icono adaptativo
    aaptOptions {
        // No compatible con adaptive icon antes de SDK 26
        additionalParameters("--no-version-vectors")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.volley:volley:1.2.1")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
