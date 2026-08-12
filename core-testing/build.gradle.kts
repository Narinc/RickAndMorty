plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}
dependencies {
    // "implementation" (test değil!) çünkü bu modülün TAMAMI zaten
    // başka modüllerin testImplementation'ında kullanılacak bir test
    // yardımcı kütüphanesi -- kendi içinde "test" ve "main" ayrımı yok.
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.junit)
    implementation(libs.turbine)
    implementation(libs.mockk)
}