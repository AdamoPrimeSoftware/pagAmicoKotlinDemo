package it.payprint.pagamico.desktop.windows.app.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle

@Composable
fun ConsoleTab(bench: BenchState) {
    var command by remember { mutableStateOf("ST") }

    Column {
        SectionTitle("Invio libero di una stringa di comando")
        FlowRow {
            Field("Comando", command, 420) { command = it }
            Cmd("Invia") { bench.exec("invio comando $command") { it.sendRaw(command) } }
        }
        Text("Nessuna attesa di risposta: quello che arriva compare nel traffico.", fontSize = 13.sp)

        SectionTitle("Comandi rapidi")
        FlowRow {
            listOf("ST", "CL", "AN", "CM", "LO", "IN000150").forEach { quick ->
                Cmd(quick) { bench.exec("invio comando $quick") { it.sendRaw(quick) } }
            }
        }
    }
}