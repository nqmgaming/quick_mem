import java.net.HttpURLConnection
import java.net.URL
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.pwhs.quickmem"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pwhs.quickmem"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val supabaseConfigFile = project.rootProject.file("supabase.properties")
        val properties = Properties()
        properties.load(supabaseConfigFile.inputStream())

        val supabaseAnoKey: String = properties.getProperty("SUPABASE_ANON_KEY") ?: ""
        val supabaseUrl: String = properties.getProperty("SUPABASE_URL") ?: ""
        val supabaseRole: String = properties.getProperty("SUPABASE_ROLE") ?: ""
        val secret: String = properties.getProperty("SECRET") ?: ""
        buildConfigField("String", "API_KEY", "\"$supabaseAnoKey\"")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ROLE", "\"$supabaseRole\"")
        buildConfigField("String", "SECRET", "\"$secret\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    tasks.whenTaskAdded {
        if (name == "assembleDebug") {
            doLast {
                println("\u0007")
                println("Telegram message sent")
            }
        }
    }
}

dependencies {
    // Compose
    implementation(libs.bundles.compose)
    implementation(platform(libs.androidx.compose.bom))

    // Serialization
    implementation(libs.bundles.serialization)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.work)

    // Superbase
    implementation(libs.bundles.supabase)

    // Ktor
    implementation(libs.bundles.ktor)

    // Coil
    implementation(libs.coil.kt.coil.compose)

    // Compose Destination
    implementation(libs.accompanist.flowlayout)
    implementation(libs.compose.destination.core)
    ksp(libs.compose.destination.ksp)

    // Paging Compose
    implementation(libs.bundles.paging.compose)

    // RichEditor
    implementation(libs.rich.editor)

    // WorkManager
    implementation(libs.androidx.work.runtime)

    // Timber
    implementation(libs.jakewharton.timber)

    implementation(libs.androidx.material.icons.extended)

    // Unit Test
    testImplementation(libs.bundles.testing)

    // Android Test
    androidTestImplementation(libs.bundles.android.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Debug Test
    debugImplementation(libs.bundles.debugging)
}