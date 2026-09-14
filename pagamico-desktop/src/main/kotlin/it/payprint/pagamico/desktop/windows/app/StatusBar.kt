package it.payprint.pagamico.desktop.windows.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.payprint.pagamico.desktop.state.BenchState

@Composable
fun StatusBar(bench: BenchState) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(12.dp).clip(CircleShape)
                .background(if (bench.connected) Color(0xFF2E7D32) else Color(0xFFC62828))
        )
        Text("  ${bench.statusText}", fontWeight = FontWeight.Medium)
        if (bench.busy) Text("   - operazione in corso...", color = Color.Gray)
    }
}