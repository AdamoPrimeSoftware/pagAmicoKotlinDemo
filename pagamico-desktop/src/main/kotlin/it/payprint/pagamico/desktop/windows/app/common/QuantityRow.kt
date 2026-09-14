package it.payprint.pagamico.desktop.windows.app.common

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun QuantityRow(state: MutableState<List<String>>) {
    FlowRow {
        state.value.forEachIndexed { i, v ->
            Field("", v, 80) { newValue ->
                state.value = state.value.toMutableList().also { it[i] = newValue }
            }
        }
    }
}