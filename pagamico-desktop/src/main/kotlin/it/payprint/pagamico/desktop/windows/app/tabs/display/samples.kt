package it.payprint.pagamico.desktop.windows.app.tabs.display

import it.payprint.pagamico.display.FontStyle
import it.payprint.pagamico.display.ListCell
import it.payprint.pagamico.display.ListData
import it.payprint.pagamico.display.ListFooter
import it.payprint.pagamico.display.ListLayout
import it.payprint.pagamico.display.ListRow
import it.payprint.pagamico.display.ListTextProperties
import it.payprint.pagamico.display.TextAlignment
import java.math.BigDecimal

fun sampleListLayout() = ListLayout(
    title = ListTextProperties(
        "ELENCO PRODOTTI ACQUISTATI",
        "FF2E86C1",
        "FFFFFFFF",
        28,
        FontStyle.BOLD,
        TextAlignment.CENTER
    ),
    body = mapOf(
        "a" to ListCell.of("Data"),
        "b" to ListCell.of("Descrizione"),
        "c" to ListCell.of("Dare", 14, TextAlignment.RIGHT),
        "d" to ListCell.of("Avere", 14, TextAlignment.RIGHT)
    ),
    footer = mapOf(
        "description" to ListCell.of("Numero articoli"),
        "total1" to ListCell.of("Imponibile", 14, TextAlignment.RIGHT),
        "total2" to ListCell.of("Imposta", 14, TextAlignment.RIGHT),
        "total3" to ListCell.of("Totale", 14, TextAlignment.RIGHT)
    )
)

fun sampleListData() = ListData(
    rows = listOf(
        ListRow("01/01/2025", "Acquisto merci", BigDecimal("120.50"), BigDecimal("0.00")),
        ListRow("02/01/2025", "Vendita prodotti", BigDecimal("0.00"), BigDecimal("200.00"))
    ),
    footer = ListFooter("2", BigDecimal("200.50"), BigDecimal("200.00"), BigDecimal("0.50"))
)