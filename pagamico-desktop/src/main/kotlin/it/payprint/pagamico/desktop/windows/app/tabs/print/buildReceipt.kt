package it.payprint.pagamico.desktop.windows.app.tabs.print

import it.payprint.pagamico.print.PagAmicoPrintJob
import it.payprint.pagamico.print.PrintAlignment
import it.payprint.pagamico.print.PrinterFont
import it.payprint.pagamico.print.PrinterFontMode
import java.math.BigDecimal

fun buildReceipt(header: String, lines: List<String>, total: BigDecimal, qr: String): PagAmicoPrintJob {
    val job = PagAmicoPrintJob()
        .reset()
        .align(PrintAlignment.CENTER)
        .font(PrinterFont.A, PrinterFontMode.DOUBLE_HEIGHT_WIDTH)
        .line(header)
        .font(PrinterFont.A, PrinterFontMode.NORMAL)
        .feed()
        .align(PrintAlignment.LEFT)
        .separator()

    lines.filter { it.isNotBlank() }.forEach { job.line(it) }

    job.separator()
        .bold()
        .line("TOTALE %20s".format(total.toPlainString()))
        .bold(false)
        .feed()

    if (qr.isNotBlank()) job.align(PrintAlignment.CENTER).qrCode(qr)
    return job.feed(2).cut()
}