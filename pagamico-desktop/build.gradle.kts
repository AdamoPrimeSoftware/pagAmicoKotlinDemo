import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

dependencies {
    implementation("it.payprint:pagamico-lib")
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "it.payprint.pagamico.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            // i primi due li suggerisce suggestRuntimeModules; jdk.net no, perche' la libreria lo
            // legge per riflessione: senza, il banco installato non regola il keepalive TCP
            modules("java.instrument", "jdk.unsupported", "jdk.net")
            packageName = "pagAmico Test Bench"
            packageVersion = "1.0.0"
        }
    }
}
