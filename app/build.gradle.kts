plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.wdevelop.game2048"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.wdevelop.game2048"

        minSdk = 23
        targetSdk = 37

        versionCode = 7
        versionName = "1.7"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true // Enables code and resource optimizations.
            }
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
}

dependencies {
    val composeBom =
        platform("androidx.compose:compose-bom:2026.08.00")

    implementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )
}
