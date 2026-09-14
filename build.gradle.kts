// :pagamico-desktop  banco di prova Compose for Desktop
//
// Richiede pagAmico_Kotlin_Lib clonato nella cartella accanto (includeBuild in settings.gradle.kts).
//
// Comandi utili:
//   gradle :pagamico-desktop:run              apre il banco di prova

plugins {
    kotlin("jvm") version "2.2.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20" apply false
    id("org.jetbrains.compose") version "1.8.2" apply false
}

allprojects {
    group = "it.payprint"
    version = "1.0.0"

    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
