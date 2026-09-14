package it.payprint.pagamico.desktop.windows.traffic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import it.payprint.pagamico.desktop.state.BenchState

/**
 * Finestra dedicata al traffico: filtri per tipo di riga, ricerca, esportazione su file
 * e accesso rapido alla cartella dei log.
 */
@Composable
fun TrafficWindow(bench: BenchState, onCloseRequest: () -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        title = "pagAmico - log della comunicazione",
        state = rememberWindowState(width = 1100.dp, height = 700.dp, position = WindowPosition(80.dp, 80.dp))
    ) {
        MaterialTheme { TrafficWindowContent(bench) }
    }
}



