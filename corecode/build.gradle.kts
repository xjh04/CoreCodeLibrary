plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jxdx.corecodelibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
// https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
// https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
// https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel
    runtimeOnly("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
// https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // https://mvnrepository.com/artifact/androidx.datastore/datastore-preferences
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.orhanobut:logger:2.2.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}