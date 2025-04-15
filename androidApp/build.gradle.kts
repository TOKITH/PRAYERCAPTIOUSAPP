plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.prayercaptious.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prayercaptious.android"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
        viewBinding = true

    }

//    dataBinding{
//        enable = true
//    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories{
        mavenCentral()
        google()

    }



}
val camerax_version:String = "1.5.0-alpha06"
dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.ui:ui-tooling:1.7.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("com.jjoe64:graphview:4.2.2")
    implementation("org.joml:joml:1.10.4")
    implementation("com.google.ar.sceneform:sceneform-base:1.17.1")

    // Face Detection
    implementation ("com.google.mlkit:face-detection:16.1.7")

    // CameraX
    implementation("com.google.android.material:material:1.12.0")
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")
    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-mlkit-vision:${camerax_version}")
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")

    // CameraSource
    implementation ("com.google.android.gms:play-services-vision-common:19.1.3")

}