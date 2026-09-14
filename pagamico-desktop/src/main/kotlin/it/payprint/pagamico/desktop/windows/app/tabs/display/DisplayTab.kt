package it.payprint.pagamico.desktop.windows.app.tabs.display

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.utils.toIntOr
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.Picker
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle
import it.payprint.pagamico.display.DisplayPosition
import it.payprint.pagamico.display.FontColor
import it.payprint.pagamico.display.FontStyle
import it.payprint.pagamico.display.KeyboardLayout
import it.payprint.pagamico.display.KeyboardMode

@Composable
fun DisplayTab(bench: BenchState) {
    var text by remember { mutableStateOf("ATTENDERE PREGO") }
    var position by remember { mutableStateOf(DisplayPosition.TOP) }
    var style by remember { mutableStateOf(FontStyle.BOLD) }
    var color by remember { mutableStateOf(FontColor.BLUE) }
    var size by remember { mutableStateOf("38") }

    var mbText by remember { mutableStateOf("Selezionare la forma di pagamento") }
    var b1 by remember { mutableStateOf("Contanti") }
    var b2 by remember { mutableStateOf("POS") }
    var b3 by remember { mutableStateOf("ANNULLA") }

    var diTitle by remember { mutableStateOf("Inserire il codice cliente") }
    var diInitial by remember { mutableStateOf("") }
    var diKeyboard by remember { mutableStateOf(KeyboardLayout.STANDARD) }

    var qrText by remember { mutableStateOf("Avvicinare il QR code al lettore ottico") }
    var qrMode by remember { mutableStateOf(KeyboardMode.NO_KEYBOARD) }

    Column {
        SectionTitle("[DT] / [DG] Finestra di testo")
        FlowRow {
            Field("Testo", text, 320) { text = it }
            Field("Dimensione", size, 110) { size = it }
        }
        FlowRow {
            Picker("Posizione", DisplayPosition.entries, position, 150) { position = it }
            Picker("Stile", FontStyle.entries, style, 170) { style = it }
            Picker("Colore", FontColor.entries, color, 150) { color = it }
        }
        FlowRow {
            Cmd("Mostra testo") {
                bench.exec("[DT/DG] finestra di testo") {
                    it.showText(text, position, size.toIntOr(38), style, color)
                }
            }
            Cmd("[DS] Chiudi") { bench.exec("[DS] chiusura finestra") { it.closeText() } }
        }

        SectionTitle("[DM] MessageBox")
        FlowRow { Field("Testo", mbText, 320) { mbText = it } }
        FlowRow {
            Field("Bottone 1", b1, 130) { b1 = it }
            Field("Bottone 2", b2, 130) { b2 = it }
            Field("Bottone 3", b3, 130) { b3 = it }
        }
        FlowRow {
            Cmd("Mostra MessageBox") {
                bench.exec("[DM] messagebox") {
                    val button = it.showMessageBox(mbText, b1, b2, b3)
                    bench.ok("bottone premuto: BT$button")
                }
            }
            Cmd("[DC] Chiudi") { bench.exec("[DC] chiusura messagebox") { it.closeMessageBox() } }
        }

        SectionTitle("[DI] Finestra di input")
        FlowRow {
            Field("Titolo", diTitle, 260) { diTitle = it }
            Field("Testo iniziale", diInitial, 160) { diInitial = it }
            Picker("Tastiera", KeyboardLayout.entries, diKeyboard, 210) { diKeyboard = it }
        }
        Cmd("Chiedi input") {
            bench.exec("[DI] finestra di input") {
                val value = it.readInput(diTitle, diInitial, diKeyboard)
                bench.ok(if (value == null) "input annullato dall'utente" else "testo digitato: $value")
            }
        }

        SectionTitle("[QR] Lettura codice")
        FlowRow {
            Field("Messaggio", qrText, 320) { qrText = it }
            Picker("Modo", KeyboardMode.entries, qrMode, 240) { qrMode = it }
        }
        FlowRow {
            Cmd("Avvia lettura") {
                bench.exec("[QR] lettura codice") {
                    val code = it.readCode(qrText, qrMode)
                    bench.ok(if (code == null) "lettura annullata dall'utente" else "codice letto: $code")
                }
            }
            Cmd("[QA] Chiudi") { bench.exec("[QA] chiusura lettura") { it.closeCodeReader() } }
        }

        SectionTitle("[TS] / [ID] / [CO] Lista sul display")
        FlowRow {
            Cmd("Invia lista di esempio") {
                bench.exec("[TS]+[ID] lista di esempio") {
                    it.showListLayout(sampleListLayout().toCompactJson())
                    it.showListData(sampleListData().toCompactJson())
                    bench.info("lista inviata: premere Esci sul display (risposta EX)")
                }
            }
            Cmd("[CO] Chiudi lista") { bench.exec("[CO] chiusura lista") { it.closeList() } }
        }

        SectionTitle("Immagini")
        FlowRow {
            Cmd("[SF] Invia logo...") { bench.sendImage(logo = true) }
            Cmd("[SI] Immagine temporanea...") { bench.sendImage(logo = false) }
            Cmd("[SR] Rimuovi immagine") { bench.exec("[SR] rimozione immagine") { it.removeTemporaryImage() } }
        }
    }
}