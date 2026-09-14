package it.payprint.pagamico.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.windows.app.App
import it.payprint.pagamico.desktop.windows.traffic.TrafficWindow

fun main() = application {
    val scope = rememberCoroutineScope()
    val bench = remember { BenchState(scope) }
    var showTrafficWindow by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = {
            bench.disconnect()
            bench.fileLogger.close()
            exitApplication()
        },
        title = "pagAmico - banco di prova (pagamico-lib)",
        state = rememberWindowState(width = 1180.dp, height = 900.dp)
    ) {
        MaterialTheme {
            App(bench) { showTrafficWindow = true }
        }
    }

    if (showTrafficWindow) {
        TrafficWindow(bench) { showTrafficWindow = false }
    }
}


