package it.payprint.pagamico.desktop.windows.app.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Cmd(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(end = 8.dp, bottom = 6.dp)) { Text(text) }
}