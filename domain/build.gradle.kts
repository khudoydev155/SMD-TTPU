import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Sof Kotlin (JVM) moduli: classpath'da na ma'lumotlar qatlami, na Android bor.
// Dependency Rule shu yerda KOMPILYATOR darajasida majburiy qilinadi.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
