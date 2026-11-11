import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.paparazzi)
}

// Supabase credentials: local.properties (local builds) or environment (CI). Never committed.
val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
}
fun secret(prop: String, env: String, default: String): String =
    localProps.getProperty(prop) ?: System.getenv(env) ?: default

android {
    namespace = "com.github.ljarka.toad"
    // 37 is required by current AndroidX/Compose; targetSdk stays 35 (no behaviour changes).
    compileSdk = 37

    defaultConfig {
        applicationId = "com.github.ljarka.toad"
        minSdk = 26
        targetSdk = 35
        // Set by the release workflow from the git tag; local builds are 1 / "dev".
        versionCode = System.getenv("TOAD_VERSION_CODE")?.toInt() ?: 1
        versionName = System.getenv("TOAD_VERSION_NAME") ?: "dev"

        buildConfigField("String", "SUPABASE_URL", "\"${secret("supabase.url", "SUPABASE_URL", "https://placeholder.supabase.co")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${secret("supabase.anon_key", "SUPABASE_ANON_KEY", "placeholder")}\"")
    }

    // Release signing comes from the environment (GitHub secrets in the release
    // workflow). Without it, release builds fall back to the debug key so they
    // still build locally.
    val releaseKeystore = System.getenv("TOAD_KEYSTORE_PATH")?.let(::file)?.takeIf { it.exists() }
    signingConfigs {
        if (releaseKeystore != null) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = System.getenv("TOAD_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("TOAD_KEY_ALIAS")
                keyPassword = System.getenv("TOAD_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName(if (releaseKeystore != null) "release" else "debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
}

// AGP 9 builds Kotlin itself (no org.jetbrains.kotlin.android plugin).
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}
