import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.pwhs.quickmem"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pwhs.quickmem"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val localProperties = Properties()
        file(rootProject.file("local.properties")).inputStream().use { localProperties.load(it) }

        val baseUrl: String = localProperties.getProperty("BASE_URL") ?: "https://api.quickmem.app/"
        val emailVerificationUrl: String = localProperties.getProperty("EMAIL_VERIFICATION_URL")
            ?: "https://checkemail.quickmem.app/"
        val bannerAdsId: String =
            localProperties.getProperty("BANNER_ADS_ID") ?: "ca-app-pub-3940256099942544/9214589741"
        val interstitialAdsId: String = localProperties.getProperty("INTERSTITIAL_ADS_ID")
            ?: "ca-app-pub-3940256099942544/1033173712"
        val rewardAdsId: String =
            localProperties.getProperty("REWARD_ADS_ID") ?: "ca-app-pub-3940256099942544/5224354917"
        val rewardedInterstitialAdsId: String =
            localProperties.getProperty("REWARDED_INTERSTITIAL_ADS_ID")
                ?: "ca-app-pub-3940256099942544/5354046379"

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "EMAIL_VERIFICATION_URL", "\"$emailVerificationUrl\"")
        buildConfigField("String", "BANNER_ADS_ID", "\"$bannerAdsId\"")
        buildConfigField("String", "INTERSTITIAL_ADS_ID", "\"$interstitialAdsId\"")
        buildConfigField("String", "REWARD_ADS_ID", "\"$rewardAdsId\"")
        buildConfigField("String", "REWARDED_INTERSTITIAL_ADS_ID", "\"$rewardedInterstitialAdsId\"")

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
}

dependencies {
    implementation("io.github.mr0xf00:easycrop:0.1.1")
    implementation("io.ak1:drawbox:1.0.3")
    implementation("io.ak1:rang-vikalp:1.0.0-alpha02")
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.2")
    implementation("io.github.ehsannarmani:compose-charts:0.0.14")

    implementation(libs.play.services.ads)

    // Compose
    implementation(libs.bundles.compose)
    implementation(platform(libs.androidx.compose.bom))
    // Serialization
    implementation(libs.bundles.serialization)
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.work)
    // Ktor
    implementation(libs.bundles.ktor)
    // Coil
    implementation(libs.coil.kt.coil.compose)
    // Compose Destination
    implementation(libs.accompanist.flowlayout)
    implementation(libs.compose.destination.animation.core)
    implementation(libs.compose.destination.core)
    ksp(libs.compose.destination.ksp)
    // WorkManager
    implementation(libs.androidx.work.runtime)
    // Timber
    implementation(libs.jakewharton.timber)
    // Retrofit
    implementation(libs.bundles.retrofit)
    // Easy validator
    implementation(libs.easyvalidation.core)
    // DataStore
    implementation(libs.bundles.datastore)
    // Unit Test
    testImplementation(libs.bundles.testing)
    // Android Test
    androidTestImplementation(libs.bundles.android.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    // Debug Test
    debugImplementation(libs.bundles.debugging)

}