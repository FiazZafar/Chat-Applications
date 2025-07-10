import com.android.build.api.dsl.ViewBinding

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.fyp.mychat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fyp.mychat"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES"
            )
        }
    }


    buildFeatures {
        viewBinding = true
    }
}
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.22.0")
    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation ("androidx.lifecycle:lifecycle-process:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-runtime:2.6.2")
}