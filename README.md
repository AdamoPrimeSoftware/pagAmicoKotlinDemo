# pagAmico — banco di prova Kotlin

Banco di prova **Compose for Desktop** per la cassa **PayPrint pagAmico**: tutti i comandi, finestra di log con filtri TX / RX / note / errori, diagnostica interna della libreria.

## Requisito: la libreria accanto

La libreria arriva dal repository **pagAmico_Kotlin_Lib** tramite `includeBuild("../pagAmico_Kotlin_Lib")` in `settings.gradle.kts`. I due repository vanno clonati **nella stessa cartella**:

```
payPrint/
  pagAmico_Kotlin_Lib/
  pagAmico_Kotlin_Demo/
```

## Avvio

In IntelliJ IDEA: configurazione **3 - Banco di prova (Compose)**. Da riga di comando:

```bash
./gradlew :pagamico-desktop:run
```

Per provare senza macchina: avviare il pagAmico Dev Kit (Simulatore → Avvia, porta 9100) e premere **Simulatore locale** nel banco.

Pacchetto installabile Windows:

```bash
./gradlew :pagamico-desktop:packageExe
```

Richiede JDK 17 aggiornato: versioni verificate e impostazione del JDK di Gradle nel README di **pagAmico_Kotlin_Lib**.
