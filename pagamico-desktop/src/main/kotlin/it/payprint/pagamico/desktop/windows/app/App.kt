package it.payprint.pagamico.desktop.windows.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.windows.app.tabs.CashTab
import it.payprint.pagamico.desktop.windows.app.tabs.CollectTab
import it.payprint.pagamico.desktop.windows.app.tabs.ConnectionTab
import it.payprint.pagamico.desktop.windows.app.tabs.ConsoleTab
import it.payprint.pagamico.desktop.windows.app.tabs.display.DisplayTab
import it.payprint.pagamico.desktop.windows.app.tabs.PosTab
import it.payprint.pagamico.desktop.windows.app.tabs.print.PrintTab
import it.payprint.pagamico.desktop.windows.app.tabs.ReloadTab
import it.payprint.pagamico.desktop.windows.app.tabs.SystemTab

@Composable
fun App(bench: BenchState, onOpenTrafficWindow: () -> Unit) {
    var tab by remember { mutableStateOf(0) }

    val tabs = listOf(
        "Connessione", "Incasso", "Contanti", "Ricariche", "POS", "Display", "Stampa", "Sistema", "Console"
    )

    Scaffold(topBar = { StatusBar(bench) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = tab, edgePadding = 8.dp) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
                }
            }

            Box(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(12.dp)) {
                when (tab) {
                    0 -> ConnectionTab(bench)
                    1 -> CollectTab(bench)
                    2 -> CashTab(bench)
                    3 -> ReloadTab(bench)
                    4 -> PosTab(bench)
                    5 -> DisplayTab(bench)
                    6 -> PrintTab(bench)
                    7 -> SystemTab(bench)
                    8 -> ConsoleTab(bench)
                }
            }

            HorizontalDivider(Modifier.Companion, DividerDefaults.Thickness, DividerDefaults.color)
            TrafficPanel(bench, onOpenTrafficWindow, Modifier.height(300.dp))
        }
    }
}