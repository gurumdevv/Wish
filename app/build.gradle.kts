import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties").let {
    it.inputStream().use { input ->
        localProperties.load(input)
    }
}
val googleClientId: String = localProperties.getProperty("google_client_id") ?: ""
val googleSdkKey: String = localProperties.getProperty("google_sdk_key") ?: ""
val projectId: String = localProperties.getProperty("project_id") ?: ""

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties").let {
    it.inputStream().use { input ->
        keystoreProperties.load(input)
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("store_file"))
            storePassword = keystoreProperties.getProperty("store_password") ?: ""
            keyAlias = keystoreProperties.getProperty("key_alias") ?: ""
            keyPassword = keystoreProperties.getProperty("key_password") ?: ""
        }
    }
    namespace = "com.gurumlab.wish"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gurumlab.wish"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"$googleClientId\"")
        buildConfigField("String", "GOOGLE_SDK_KEY", "\"$googleSdkKey\"")
        buildConfigField("String", "PROJECT_ID", "\"$projectId\"")
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            versionNameSuffix = "-release"
        }
        debug {
            isMinifyEnabled = false
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation("com.google.auth:google-auth-library-oauth2-http:1.15.0") {
        exclude(group = "io.grpc")
    }
    implementation(libs.firebase.messaging)
    implementation(libs.google.firebase.analytics)
    implementation(libs.accompanist.permissions)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.firebase.storage)
    implementation(libs.compose.shimmer)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    ksp(libs.hilt.compiler)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)
    implementation(libs.gson)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}