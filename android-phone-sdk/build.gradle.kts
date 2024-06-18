plugins {
    id("com.android.library")
}

android {
    namespace = "com.atlasat.android_phone_sdk"
    compileSdk = 34

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")

    //Linphone SDK
    // Latest version is 5.0.x, using + to get the latest available
    //noinspection GradleDynamicVersion
    implementation("org.linphone:linphone-sdk-android:5.4+")
    // Adding this dependency allows the linphone-sdk to automatically handle audio focus
    implementation("androidx.media:media:1.7.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}