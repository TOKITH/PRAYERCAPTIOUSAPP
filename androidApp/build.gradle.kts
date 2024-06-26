plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.prayercaptious.android"
    compileSdk = 34

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

dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-tooling:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.compose.foundation:foundation:1.4.3")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("com.jjoe64:graphview:4.2.2")
    implementation("org.joml:joml:1.10.4")
    implementation("com.google.ar.sceneform:sceneform-base:1.17.1")

}