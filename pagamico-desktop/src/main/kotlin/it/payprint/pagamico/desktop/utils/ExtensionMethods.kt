package it.payprint.pagamico.desktop.utils

import androidx.compose.ui.graphics.Color
import it.payprint.pagamico.desktop.state.LogKind
import java.math.BigDecimal
import java.text.DecimalFormat


fun LogKind.prefixOf(): String = when (this) {
    LogKind.TX -> "TX >"
    LogKind.RX -> "RX <"
    LogKind.INFO -> "  i "
    LogKind.OK -> "  + "
    LogKind.ERROR -> "ERR!"
}

/** Colore della riga in base al tipo, condiviso fra pannello e finestra di log. */
fun LogKind.colorOf(): Color = when (this) {
    LogKind.TX -> Color(0xFF1565C0)
    LogKind.RX -> Color.Black
    LogKind.INFO -> Color(0xFF37474F)
    LogKind.OK -> Color(0xFF2E7D32)
    LogKind.ERROR -> Color(0xFFC62828)
}

/**
 * Importo con due decimali e il separatore decimale della lingua del PC ("800,00"): e' il formato
 * `0.00` del banco WinForms, cosi' i due banchi scrivono le stesse righe di log.
 */
fun BigDecimal?.eur(): String = DecimalFormat("0.00").format(this ?: BigDecimal.ZERO)

fun String.toDecimal(default: String = "0"): BigDecimal =
    runCatching { BigDecimal(this.replace(',', '.').trim()) }.getOrElse { BigDecimal(default) }

fun String.toIntOr(default: Int) = trim().toIntOrNull() ?: default