package it.payprint.pagamico.desktop.windows.app.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle

@Composable
fun ConnectionTab(bench: BenchState) = Column {
    SectionTitle("Collegamento al pagAmico o al simulatore")
    FlowRow {
        Field("Indirizzo IP", bench.host) { bench.host = it }
        Field("Porta", bench.port, 100) { bench.port = it }
    }
    FlowRow {
        Cmd("Connetti") { bench.connect() }
        Cmd("Disconnetti") { bench.disconnect() }
        Cmd("Simulatore locale") {
            bench.host = "127.0.0.1"
            bench.port = "9100"
            bench.connect()
        }
    }

    SectionTitle("Opzioni protocollo")
    FlowRow {
        Field("Terminatore (vuoto = nessuno)", bench.terminator, 230) { bench.terminator = it }
        Field("Password erogazione", bench.password, 200) { bench.password = it }
    }

    SectionTitle("Log della comunicazione")
    Text("File corrente: ${bench.logFilePath}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    FlowRow {
        Checkbox(checked = bench.logToFile, onCheckedChange = { bench.logToFile = it })
        Text("registra su file", modifier = Modifier.padding(top = 12.dp, end = 12.dp))
        Cmd("Apri cartella log") { bench.openLogFolder() }
    }

    SectionTitle("Promemoria")
    Text("Simulatore Dev Kit: sezione Simulatore -> Avvia, poi qui Connetti su 127.0.0.1.", fontSize = 13.sp)
    Text("Dispositivo reale: IP nel Menu Servizi del pagAmico. Porta di fabbrica 9100.", fontSize = 13.sp)
    Text("Nessuna risposta prevista per: CL, DS, DC, QA, CO, DT, DG, TS.", fontSize = 13.sp)
}