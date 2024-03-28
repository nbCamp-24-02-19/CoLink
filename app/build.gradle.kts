import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.seven.colink"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.seven.colink"
        minSdk = 26
        targetSdk = 34
        versionCode = 6
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["kakaoAppKey"] = properties["KAKAO_APP_KEY"] as String
        buildConfigField("String", "ALGOLIA_API_KEY", properties.getProperty("ALGOLIA_API_KEY"))
        buildConfigField("String", "ALGOLIA_APP_ID", properties.getProperty("ALGOLIA_APP_ID"))
        buildConfigField("String", "FCM_KEY", properties.getProperty("FCM_KEY"))
        buildConfigField("String", "ADMIN_UID", properties.getProperty("ADMIN_UID"))
        buildConfigField("String", "GOOGLE_SIGN", properties.getProperty("GOOGLE_SIGN"))
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.3")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //by viewModels를 사용하기 위한 의존성
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // coil
    implementation("io.coil-kt:coil:2.3.0")

    //hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.50")

    //lottie
    implementation("com.airbnb.android:lottie:6.3.0")

    //retrofit
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")
    //FCM 라이브러리
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    //mock
    testImplementation("org.mockito:mockito-core:5.3.0")

    //material library
    implementation("com.google.android.material:material:1.11.0")

    //algolia
    implementation("com.algolia:algoliasearch-android:3.+")
    implementation("com.algolia:algoliasearch-client-kotlin:2.1.9")

    // Indicator
    implementation("com.tbuonomo:dotsindicator:5.0")

    //Jsoup
    implementation("org.jsoup:jsoup:1.17.2")

    // Calendar
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.0")

    //google
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    //kakao
    implementation ("com.kakao.sdk:v2-all:2.20.1") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation ("com.kakao.sdk:v2-user:2.20.1") // 카카오 로그인 API 모듈
    implementation ("com.kakao.sdk:v2-cert:2.20.1") // 카카오톡 인증 서비스 API 모듈

    //firebase Function
    implementation ("com.google.firebase:firebase-functions-ktx:20.4.0")
}