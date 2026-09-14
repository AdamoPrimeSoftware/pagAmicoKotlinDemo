package it.payprint.pagamico.desktop.windows.app.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import it.payprint.pagamico.desktop.state.BenchState
import it.payprint.pagamico.desktop.utils.toDecimal
import it.payprint.pagamico.desktop.utils.toIntOr
import it.payprint.pagamico.desktop.windows.app.common.QuantityRow
import it.payprint.pagamico.desktop.windows.app.common.Cmd
import it.payprint.pagamico.desktop.windows.app.common.Field
import it.payprint.pagamico.desktop.windows.app.common.SectionTitle
import it.payprint.pagamico.response.PagAmicoResponse

@Composable
fun CollectTab(bench: BenchState) {
    var amount by remember { mutableStateOf("1.50") }
    var timeoutSeconds by remember { mutableStateOf("20") }
    var payout by remember { mutableStateOf("5.00") }
    val notes = remember { mutableStateOf(listOf("0", "0", "0", "0", "0", "0")) }
    val coins = remember { mutableStateOf(listOf("0", "0", "0", "0", "0", "0")) }

    val onPartial: (PagAmicoResponse) -> Unit = { p ->
        bench.info("parziale: incassato ${p.collectedAmount} (monete ${p.collectedCoins}, banconote ${p.collectedBanknotes}), da incassare ${p.amountToCollect}")
    }

    Column {
        SectionTitle("Incasso")
        FlowRow {
            Field("Importo EUR", amount, 130) { amount = it }
            Cmd("[IN] Contanti") {
                bench.execResponse("[IN] incasso contanti") {
                    it.collectCash(
                        amount.toDecimal(),
                        onPartial = onPartial
                    )
                }
            }
            Cmd("[PO] POS") {
                bench.execResponse("[PO] incasso POS") { it.collectPos(amount.toDecimal(), onPartial = onPartial) }
            }
            Cmd("[IM] Automatico") {
                bench.execResponse("[IM] incasso automatico") {
                    it.collectAuto(
                        amount.toDecimal(),
                        onPartial = onPartial
                    )
                }
            }
        }
        FlowRow {
            Field("Timeout s", timeoutSeconds, 110) { timeoutSeconds = it }
            Cmd("[I2] Incasso con timeout") {
                bench.execResponse("[I2] incasso con timeout") {
                    it.collectCashWithTimeout(amount.toDecimal(), timeoutSeconds.toIntOr(20), onPartial)
                }
            }
        }
        FlowRow {
            // a incasso aperto cancelOperation() passa per la via laterale e restituisce l'esito dell'incasso
            Cmd("[AN] Annulla") { bench.execResponse("[AN] annullo") { it.cancelOperation() } }
            Cmd("[CM] Commit") { bench.execResponse("[CM] commit") { it.commit() } }
        }

        SectionTitle("Erogazione")
        FlowRow {
            Field("Importo EUR", payout, 130) { payout = it }
            Cmd("[PA] Eroga importo") {
                bench.execResponse("[PA] erogazione") { it.dispense(payout.toDecimal(), bench.password) }
            }
        }

        SectionTitle("Banconote da erogare (5 / 10 / 20 / 50 / 100 / 200)")
        QuantityRow(notes)
        Cmd("[P2] Eroga banconote") {
            val q = notes.value.map { it.toIntOr(0) }
            bench.execResponse("[P2] erogazione banconote") {
                it.dispenseBanknotes(q[0], q[1], q[2], q[3], q[4], q[5], bench.password)
            }
        }

        SectionTitle("Monete da erogare (0,05 / 0,10 / 0,20 / 0,50 / 1 / 2)")
        QuantityRow(coins)
        Cmd("[PM] Eroga monete") {
            val q = coins.value.map { it.toIntOr(0) }
            bench.execResponse("[PM] erogazione monete") {
                it.dispenseCoins(q[0], q[1], q[2], q[3], q[4], q[5], bench.password)
            }
        }
    }
}