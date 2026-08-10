plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.narinc.rickandmorty.feature.character.data"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-network"))
    implementation(project(":character-domain"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.paging.common)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}