package it.payprint.pagamico.desktop.windows.app.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import it.payprint.pagamico.commands.MovementCause
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle
import java.time.LocalDateTime

@Composable
fun SystemTab(bench: BenchState) {
    var cause by remember { mutableStateOf(MovementCause.ALL) }
    var movementId by remember { mutableStateOf("1") }

    Column {
        SectionTitle("Sistema")
        FlowRow {
            Cmd("[CL] Pulisci display") { bench.exec("[CL] pulizia display") { it.clearDisplay() } }
            Cmd("[LO] Ultimo JSON") { bench.execResponse("[LO] rinvio ultimo JSON") { it.lastJson() } }
            Cmd("[RI] Riavvia pagAmico") { bench.exec("[RI] riavvio") { it.reboot() } }
        }

        SectionTitle("Movimenti")
        FlowRow {
            Field("Causale", cause, 110) { cause = it }
            Cmd("[MV] Movimenti di oggi") {
                bench.exec("[MV] elenco movimenti") { client ->
                    val today = LocalDateTime.now().toLocalDate()
                    val list = client.movements(today.atStartOfDay(), today.atTime(23, 59), cause.trim())
                    bench.ok("${list.size} movimenti")
                    list.take(50).forEach { bench.info("  $it") }
                }
            }
        }
        FlowRow {
            Field("Id", movementId, 130) { movementId = it }
            Cmd("[MI] Movimento per Id") {
                bench.exec("[MI] movimento per Id") { client ->
                    val m = client.movement(movementId.trim().toLongOrNull() ?: 1L)
                    bench.ok(m?.toString() ?: "nessun movimento con questo Id")
                }
            }
        }
    }
}