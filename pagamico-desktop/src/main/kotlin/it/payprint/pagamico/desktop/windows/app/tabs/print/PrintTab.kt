package it.payprint.pagamico.desktop.windows.app.tabs.print

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.utils.toDecimal
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle

@Composable
fun PrintTab(bench: BenchState) {
    var header by remember { mutableStateOf("PRIME SOFTWARE S.R.L.") }
    var body by remember { mutableStateOf("Articolo 1                 12,00\nArticolo 2                  3,50") }
    var total by remember { mutableStateOf("15.50") }
    var qr by remember { mutableStateOf("www.pagamico.it") }

    Column {
        SectionTitle("Scontrino di prova")
        FlowRow { Field("Intestazione", header, 280) { header = it } }
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Righe", fontSize = 12.sp) },
            modifier = Modifier.width(520.dp).height(140.dp)
        )
        FlowRow {
            Field("Totale", total, 120) { total = it }
            Field("QR code", qr, 240) { qr = it }
        }
        FlowRow {
            Cmd("Stampa scontrino") {
                bench.exec("stampa scontrino di prova") {
                    it.print(buildReceipt(header, body.lines(), total.toDecimal(), qr))
                }
            }
            Cmd("[PTSTAT] Stato stampante") {
                bench.exec("[PTSTAT] stato stampante") { bench.ok(it.printerStatus().toString()) }
            }
            Cmd("[PTSTAN] Annulla stampa") { bench.exec("[PTSTAN] annullo stampa") { it.cancelPrint() } }
        }

        PrintSingleCommands(bench)
    }
}