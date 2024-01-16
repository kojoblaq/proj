plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proj"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proj"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("com.google.firebase:firebase-auth:22.3.0")
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-analytics")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.4.0")

//    implementation ("com.ebanx:swipe-button:0.4.0")
    implementation ("com.soundcloud.android:android-crop:1.0.1@aar")
    implementation ("com.karumi:dexter:6.2.0")
    implementation ("com.squareup.picasso:picasso:2.5.2")

    //noinspection GradleCompatible
//    implementation ("com.android.support:design:28.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation ("androidx.recyclerview:recyclerview-selection:1.2.0-alpha01")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.github.jd-alexander:library:1.1.0")
}