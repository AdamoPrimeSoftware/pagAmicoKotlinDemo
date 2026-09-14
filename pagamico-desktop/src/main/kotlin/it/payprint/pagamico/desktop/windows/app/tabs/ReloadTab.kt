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
fun ReloadTab(bench: BenchState) {
    var updateFloat by remember { mutableStateOf(true) }
    var partials by remember { mutableStateOf(false) }

    Column {
        SectionTitle("Sessione di ricarica")
        FlowRow {
            Checkbox(checked = updateFloat, onCheckedChange = { updateFloat = it })
            Text("aggiorna il fondo cassa", modifier = Modifier.padding(top = 12.dp, end = 12.dp))
            Checkbox(checked = partials, onCheckedChange = { partials = it })
            Text("invia i parziali al client", modifier = Modifier.padding(top = 12.dp))
        }
        FlowRow {
            Cmd("[RC/RS/VC/VS] Mista") {
                bench.execResponse("ricarica mista") { it.startMixedReload(updateFloat, partials) }
            }
            Cmd("[RM/R3] Monete") { bench.execResponse("ricarica monete") { it.startCoinReload(updateFloat) } }
            Cmd("[RB/R2] Banconote") { bench.execResponse("ricarica banconote") { it.startBanknoteReload(updateFloat) } }
            Cmd("[FR] Fine ricarica") { bench.execResponse("[FR] fine ricarica") { it.endReload() } }
        }
    }
}