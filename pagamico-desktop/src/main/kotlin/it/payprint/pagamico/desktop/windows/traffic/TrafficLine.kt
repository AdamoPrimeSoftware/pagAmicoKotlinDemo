package it.payprint.pagamico.desktop.windows.traffic

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import it.payprint.pagamico.desktop.state.LogLine
import it.payprint.pagamico.desktop.utils.colorOf
import it.payprint.pagamico.desktop.utils.prefixOf

@Composable
fun TrafficLine(line: LogLine) {
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