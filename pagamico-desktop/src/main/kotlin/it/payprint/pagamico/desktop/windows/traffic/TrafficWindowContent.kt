package it.payprint.pagamico.desktop.windows.traffic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.state.LogKind
import it.payprint.pagamico.desktop.state.LogLine
import it.payprint.pagamico.desktop.utils.FILE_STAMP
import java.io.File
import java.time.LocalDateTime

@Composable
fun TrafficWindowContent(bench: BenchState) {
    var showTx by remember { mutableStateOf(true) }
    var showRx by remember { mutableStateOf(true) }
    var showInfo by remember { mutableStateOf(true) }
    var showErrors by remember { mutableStateOf(true) }
    var autoScroll by remember { mutableStateOf(true) }
    var search by remember { mutableStateOf("") }

    fun accepts(line: LogLine): Boolean {
        val kindOk = when (line.kind) {
            LogKind.TX -> showTx
            LogKind.RX -> showRx
            LogKind.ERROR -> showErrors
            else -> showInfo
        }
        return kindOk && (search.isBlank() || line.text.contains(search.trim(), ignoreCase = true))
    }

    val visible = bench.log.filter(::accepts)
    val listState = rememberLazyListState()

    LaunchedEffect(visible.size, autoScroll) {
        if (autoScroll && visible.isNotEmpty()) listState.scrollToItem(visible.lastIndex)
    }

    Column(Modifier.fillMaxSize()) {
        FlowRow(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Filter("TX", showTx) { showTx = it }
            Filter("RX", showRx) { showRx = it }
            Filter("note", showInfo) { showInfo = it }
            Filter("errori", showErrors) { showErrors = it }
            Filter("segui", autoScroll) { autoScroll = it }

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Cerca", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.width(200.dp).padding(start = 8.dp, end = 8.dp)
            )

            OutlinedButton(onClick = {
                val name = "pagamico-traffico-${LocalDateTime.now().format(FILE_STAMP)}.log"
                bench.exportLog(File(bench.fileLogger.logDirectory, name), ::accepts)
            }, modifier = Modifier.padding(end = 6.dp)) { Text("Esporta") }

            OutlinedButton(onClick = { bench.clearLog() }, modifier = Modifier.padding(end = 6.dp)) { Text("Pulisci") }
            OutlinedButton(onClick = { bench.openLogFolder() }) { Text("Cartella log") }
        }

        Row(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
            Text(
                "${bench.log.size} righe in memoria - file: ${bench.logFilePath}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        HorizontalDivider(Modifier.Companion, DividerDefaults.Thickness, DividerDefaults.color)

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp)) {
            items(visible) { line ->
                TrafficLine(line)
            }
        }
    }
}