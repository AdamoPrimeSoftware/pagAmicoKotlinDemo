package it.payprint.pagamico.desktop.windows.app.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.payprint.pagamico.commands.CashFloatTarget
import it.payprint.pagamico.commands.DenominationToggle
import it.payprint.pagamico.commands.StockThreshold
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.utils.toIntOr
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.Picker
import it.payprint.pagamico.desktop.windows.app.common.QuantityRow
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle

@Composable
fun CashTab(bench: BenchState) {
    var floatTarget by remember { mutableStateOf(CashFloatTarget.BOTH) }
    var minStock by remember { mutableStateOf("5") }
    var maxStock by remember { mutableStateOf("100") }
    var acceptAll by remember { mutableStateOf(true) }
    var changeAll by remember { mutableStateOf(true) }
    val toBta = remember { mutableStateOf(listOf("0", "0", "0", "0", "0", "0")) }
    val toCashbox = remember { mutableStateOf(listOf("0", "0", "0", "0", "0", "0")) }

    Column {
        SectionTitle("Situazione")
        FlowRow {
            Cmd("[ST] Richiesta situazione") { bench.execResponse("[ST] situazione") { it.status() } }
            Cmd("Dettaglio giacenze") { bench.dumpStock() }
        }

        SectionTitle("Fondo cassa e cassetto BTA")
        FlowRow {
            Picker("Fondo cassa", CashFloatTarget.entries, floatTarget) { floatTarget = it }
            Cmd("[AF] Aggiorna fondo cassa") {
                bench.execResponse("[AF] aggiorna fondo cassa") { it.updateCashFloat(floatTarget, bench.password) }
            }
            Cmd("[BT] Azzera BTA") { bench.execResponse("[BT] azzera BTA") { it.resetBta(bench.password) } }
            Cmd("[AZ] Azzera banconote") {
                bench.execResponse("[AZ] azzera banconote") { it.resetBanknotes(bench.password) }
            }
        }

        SectionTitle("Soglie di scorta (min / max per ogni taglio)")
        FlowRow {
            Field("min", minStock, 90) { minStock = it }
            Field("max", maxStock, 90) { maxStock = it }
            Cmd("[SM] Soglie monete") {
                bench.execResponse("[SM] soglie monete") {
                    it.setCoinStock(List(6) { _ -> StockThreshold(minStock.toIntOr(0), maxStock.toIntOr(100)) })
                }
            }
            Cmd("[SB] Soglie banconote") {
                bench.execResponse("[SB] soglie banconote") {
                    it.setBanknoteStock(List(6) { _ -> StockThreshold(minStock.toIntOr(0), maxStock.toIntOr(100)) })
                }
            }
        }

        SectionTitle("Abilitazione tagli")
        FlowRow(verticalArrangement = Arrangement.Center) {
            Checkbox(checked = acceptAll, onCheckedChange = { acceptAll = it })
            Text("accetta in incasso", modifier = Modifier.padding(top = 12.dp, end = 12.dp))
            Checkbox(checked = changeAll, onCheckedChange = { changeAll = it })
            Text("erogabile come resto", modifier = Modifier.padding(top = 12.dp, end = 12.dp))
        }
        FlowRow {
            Cmd("[EM] Tagli monete") {
                bench.execResponse("[EM] abilitazione monete") {
                    it.setCoinAcceptance(List(6) { _ -> DenominationToggle(acceptAll, changeAll) })
                }
            }
            Cmd("[EB] Tagli banconote") {
                bench.execResponse("[EB] abilitazione banconote") {
                    it.setBanknoteAcceptance(List(6) { _ -> DenominationToggle(acceptAll, changeAll) })
                }
            }
        }

        SectionTitle("Banconote da spostare in BTA (5 / 10 / 20 / 50 / 100 / 200)")
        QuantityRow(toBta)
        Cmd("[M2] Sposta in BTA") {
            val q = toBta.value.map { it.toIntOr(0) }
            bench.execResponse("[M2] spostamento in BTA") {
                it.moveBanknotesToBta(q[0], q[1], q[2], q[3], q[4], q[5], bench.password)
            }
        }

        SectionTitle("Monete da spostare nel cassetto di recupero (0,05 ... 2)")
        QuantityRow(toCashbox)
        Cmd("[MF] Sposta monete") {
            val q = toCashbox.value.map { it.toIntOr(0) }
            bench.execResponse("[MF] spostamento monete") {
                it.moveCoinsToCashbox(q[0], q[1], q[2], q[3], q[4], q[5], bench.password)
            }
        }
    }
}