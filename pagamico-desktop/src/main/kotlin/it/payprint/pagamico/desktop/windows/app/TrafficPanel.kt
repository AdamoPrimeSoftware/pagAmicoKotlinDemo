package it.payprint.pagamico.desktop.windows.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.utils.colorOf
import it.payprint.pagamico.desktop.utils.prefixOf
import it.payprint.pagamico.desktop.windows.app.common.Spacer8

@Composable
fun TrafficPanel(bench: BenchState, onOpenTrafficWindow: () -> Unit, modifier: Modifier = Modifier.Companion) {
    val listState = rememberLazyListState()

    LaunchedEffect(bench.log.size) {
        if (bench.log.isNotEmpty()) listState.scrollToItem(bench.log.lastIndex)
    }

    Column(modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Traffico", fontWeight = FontWeight.Bold)
            Spacer8()
            OutlinedButton(onClick = onOpenTrafficWindow) { Text("Finestra log") }
            Spacer8()
            OutlinedButton(onClick = { bench.clearLog() }) { Text("Pulisci") }
            Spacer8()
            Checkbox(checked = bench.logToFile, onCheckedChange = { bench.logToFile = it })
            Text("registra su file", fontSize = 13.sp)
            Spacer8()
            Checkbox(checked = bench.showTrace, onCheckedChange = { bench.showTrace = it })
            Text("diagnostica libreria", fontSize = 13.sp)
        }
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp)) {
            items(bench.log) { line ->
                Row {
                    Text(line.time, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "  ${line.kind.prefixOf()} ${line.text}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = line.kind.colorOf()
                    )
                }
            }
        }
    }
}