package it.payprint.pagamico.desktop.windows.app.tabs.print

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.utils.toIntOr
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.Picker
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle
import it.payprint.pagamico.print.BarcodeTextPosition
import it.payprint.pagamico.print.BarcodeType
import it.payprint.pagamico.print.PagAmicoPrint
import it.payprint.pagamico.print.PrintAlignment
import it.payprint.pagamico.print.PrinterFont
import it.payprint.pagamico.print.PrinterFontMode
import it.payprint.pagamico.print.Underline

@Composable
fun PrintSingleCommands(bench: BenchState) {
    var font by remember { mutableStateOf(PrinterFont.A) }
    var fontMode by remember { mutableStateOf(PrinterFontMode.NORMAL) }
    var alignment by remember { mutableStateOf(PrintAlignment.LEFT) }
    var codePage by remember { mutableStateOf("0") }
    var feedLines by remember { mutableStateOf("3") }
    var freeText by remember { mutableStateOf("prova di stampa") }
    var repeatChar by remember { mutableStateOf("-") }
    var repeatCount by remember { mutableStateOf("20") }
    var barcodeType by remember { mutableStateOf(BarcodeType.EAN13) }
    var barcodeValue by remember { mutableStateOf("876543210123") }
    var barcodeHeight by remember { mutableStateOf("100") }
    var barcodeTextPos by remember { mutableStateOf(BarcodeTextPosition.BELOW) }
    var escPos by remember { mutableStateOf("prova ESC/POS") }

    fun send(command: String) = bench.exec("invio comando $command") { it.sendRaw(command) }

    SectionTitle("Comandi singoli - stile")
    FlowRow {
        Cmd("[PTBDON] Grassetto") { send(PagAmicoPrint.bold(true)) }
        Cmd("[PTBDOF] Fine grassetto") { send(PagAmicoPrint.bold(false)) }
        Cmd("[PTITON] Corsivo") { send(PagAmicoPrint.italic(true)) }
        Cmd("[PTITOF] Fine corsivo") { send(PagAmicoPrint.italic(false)) }
    }
    FlowRow {
        Cmd("[PTDBON] Doppia grandezza") { send(PagAmicoPrint.doubleSize(true)) }
        Cmd("[PTDBOF] Fine doppia") { send(PagAmicoPrint.doubleSize(false)) }
        Cmd("[PTUN01] Sottolineato") { send(PagAmicoPrint.underline(Underline.SINGLE)) }
        Cmd("[PTUN00] Fine sottolineato") { send(PagAmicoPrint.underline(Underline.NONE)) }
    }

    SectionTitle("Comandi singoli - font e layout")
    FlowRow {
        Picker("Font", PrinterFont.entries, font, 110) { font = it }
        Picker("Modo", PrinterFontMode.entries, fontMode, 230) { fontMode = it }
        Cmd("[PTFA/PTFB] Imposta font") { send(PagAmicoPrint.font(font, fontMode)) }
    }
    FlowRow {
        Picker("Allineamento", PrintAlignment.entries, alignment, 190) { alignment = it }
        Cmd("[PTJT] Imposta allineamento") { send(PagAmicoPrint.align(alignment)) }
    }
    FlowRow {
        Field("Code page", codePage, 110) { codePage = it }
        Cmd("[PTCP] Imposta code page") { send(PagAmicoPrint.codePage(codePage.toIntOr(0))) }
        Field("Righe", feedLines, 100) { feedLines = it }
        Cmd("[PTLFNR] Avanza") { send(PagAmicoPrint.lineFeed(feedLines.toIntOr(1))) }
    }

    SectionTitle("Comandi singoli - contenuto")
    FlowRow {
        Field("Testo", freeText, 300) { freeText = it }
        Cmd("[PTPRWL] Riga + a capo") { send(PagAmicoPrint.writeLine(freeText)) }
        Cmd("[PTPRWR] Solo testo") { send(PagAmicoPrint.write(freeText)) }
    }
    FlowRow {
        Field("Carattere", repeatChar, 100) { repeatChar = it }
        Field("Volte", repeatCount, 100) { repeatCount = it }
        Cmd("[PTPRRE] Ripeti") {
            send(PagAmicoPrint.repeatChar(repeatChar.firstOrNull() ?: '-', repeatCount.toIntOr(20)))
        }
        Cmd("[PTPRRL] Ripeti + a capo") {
            send(PagAmicoPrint.repeatCharLine(repeatChar.firstOrNull() ?: '-', repeatCount.toIntOr(20)))
        }
    }
    FlowRow {
        Picker("Tipo", BarcodeType.entries, barcodeType, 170) { barcodeType = it }
        Field("Valore", barcodeValue, 190) { barcodeValue = it }
        Field("Altezza", barcodeHeight, 110) { barcodeHeight = it }
        Picker("Testo", BarcodeTextPosition.entries, barcodeTextPos, 160) { barcodeTextPos = it }
        Cmd("[PTBCBC] Barcode") {
            send(PagAmicoPrint.barcode(barcodeType, barcodeValue, barcodeHeight.toIntOr(100), barcodeTextPos))
        }
    }
    FlowRow {
        Field("ESC/POS", escPos, 300) { escPos = it }
        Cmd("[PTPRDT] Invio ESC/POS") { send(PagAmicoPrint.rawEscPos(escPos)) }
    }

    SectionTitle("Comandi singoli - servizio")
    FlowRow {
        Cmd("[PTRSET] Reset") { send(PagAmicoPrint.reset()) }
        Cmd("[PTCUPT] Taglio parziale") { send(PagAmicoPrint.cut(false)) }
        Cmd("[PTCUTL] Taglio totale") { send(PagAmicoPrint.cut(true)) }
        Cmd("[PTSTST] Inizio stampa") { send(PagAmicoPrint.beginPrint()) }
        Cmd("[PTSTEN] Fine stampa") { send(PagAmicoPrint.endPrint()) }
    }
}