plugins {
    kotlin("jvm") version "2.0.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // mainClass = "com.rnimour.sorters.mergesort.MergeSortKt"
    // mainClass = "com.rnimour.sorters.mergesort.coroutines/CoMergeSortKt"
    mainClass = "com.rnimour.sorters.mergesort.coroutines/SmartCoMergeSortKt"
}
