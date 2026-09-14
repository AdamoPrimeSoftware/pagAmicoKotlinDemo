package it.payprint.pagamico.desktop.windows.app.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle

@Composable
fun PosTab(bench: BenchState) {
    var reprint by remember { mutableStateOf(false) }

    Column {
        SectionTitle("Comandi POS")
        FlowRow {
            Checkbox(checked = reprint, onCheckedChange = { reprint = it })
            Text("ristampa scontrino", modifier = Modifier.padding(top = 12.dp, end = 12.dp))
            Cmd("[PL] Ultima transazione") {
                bench.execResponse("[PL] ultima transazione POS") { it.readLastPosTransaction(reprint) }
            }
        }
        FlowRow {
            Cmd("[PR] Totali POS") { bench.execResponse("[PR] totali POS") { it.readPosTotals() } }
            Cmd("[PS] Chiusura giornaliera") { bench.execResponse("[PS] chiusura giornaliera") { it.closePosDay() } }
            Cmd("[PZ] Riavvio POS") { bench.execResponse("[PZ] riavvio POS") { it.rebootPos() } }
            Cmd("[PP] Primo DLL") { bench.execResponse("[PP] primo DLL") { it.posFirstDll() } }
        }

        SectionTitle("Ultimo messaggio di fine transazione")
        Cmd("Mostra scontrino POS") {
            val msg = bench.lastResponse?.posFinancialTransactionEndResponseMessage
            if (msg.isNullOrEmpty()) {
                bench.error("nessun messaggio POS nell'ultima risposta")
            } else {
                msg.replace(2.toChar().toString(), "<STX>")
                    .replace(3.toChar().toString(), "<ETX>")
                    .replace(23.toChar().toString(), "<ETB>")
                    .split('\n')
                    .forEach { bench.info("  " + it.trimEnd('\r')) }
            }
        }
    }
}