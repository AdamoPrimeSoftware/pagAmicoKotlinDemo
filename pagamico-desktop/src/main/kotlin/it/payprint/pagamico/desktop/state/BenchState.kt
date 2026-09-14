package it.payprint.pagamico.desktop.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import it.payprint.pagamico.client.PagAmicoClient
import it.payprint.pagamico.client.PagAmicoErrorCodes
import it.payprint.pagamico.exceptions.PagAmicoException
import it.payprint.pagamico.client.PagAmicoFileLogger
import it.payprint.pagamico.desktop.utils.IMMAGINE_GRANDE
import it.payprint.pagamico.desktop.utils.prefixOf
import it.payprint.pagamico.response.PagAmicoResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Stato del banco di prova: connessione, traffico e helper per eseguire i comandi
 * riportando l'esito nel log.
 */
class BenchState(private val scope: CoroutineScope) {

    private val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    val log = mutableStateListOf<LogLine>()

    var host by mutableStateOf("127.0.0.1")
    var port by mutableStateOf("9100")
    var terminator by mutableStateOf("")
    var password by mutableStateOf("")

    var connected by mutableStateOf(false)
        private set

    var statusText by mutableStateOf("disconnesso")
        private set

    var busy by mutableStateOf(false)
        private set

    var lastResponse by mutableStateOf<PagAmicoResponse?>(null)
        private set

    private var client: PagAmicoClient? = null
    private var currentJob: Job? = null

    /** Logger su file: un file al giorno in %LOCALAPPDATA%\PayPrint.PagAmico\logs */
    val fileLogger = PagAmicoFileLogger(prefix = "compose")

    var logToFile by mutableStateOf(true)

    /** Diagnostica interna della libreria nel traffico: utile quando un comando non risponde. */
    var showTrace by mutableStateOf(false)

    val logFilePath: String get() = fileLogger.currentFile.absolutePath

    @Suppress("unused")
    val logDirectoryPath: String get() = fileLogger.logDirectory.absolutePath

    // ---------------------------------------------------------------- log

    private fun append(kind: LogKind, text: String) {
        log.add(LogLine(LocalTime.now().format(timeFormat), kind, text))
        if (log.size > 2000) repeat(500) { log.removeAt(0) }

        fileLogger.enabled = logToFile
        fileLogger.write(kind.prefixOf().trim(), text)
    }

    fun info(text: String) = append(LogKind.INFO, text)
    fun ok(text: String) = append(LogKind.OK, text)
    fun error(text: String) = append(LogKind.ERROR, text)
    fun clearLog() = log.clear()

    /** Righe attualmente in memoria, nello stesso formato del file. */
    fun logAsText(filter: (LogLine) -> Boolean = { true }): String =
        log.filter(filter).joinToString(System.lineSeparator()) { "${it.time} ${it.kind.prefixOf()} ${it.text}" }

    /** Esporta le righe in memoria in un file scelto dall'utente. */
    fun exportLog(target: File, filter: (LogLine) -> Boolean = { true }) {
        runCatching { target.writeText(logAsText(filter)) }
            .onSuccess { ok("log esportato in ${target.absolutePath}") }
            .onFailure { error("esportazione fallita: ${it.message}") }
    }

    /** Apre la cartella dei log nel gestore file di sistema. */
    fun openLogFolder() {
        runCatching {
            fileLogger.logDirectory.mkdirs()
            Desktop.getDesktop().open(fileLogger.logDirectory)
        }.onFailure { error("impossibile aprire la cartella: ${it.message}") }
    }

    // ---------------------------------------------------------------- connessione

    fun connect() {
        disconnect()
        scope.launch {
            try {
                val c = PagAmicoClient(
                    host = host.trim(),
                    port = port.trim().toIntOrNull() ?: PagAmicoClient.DEFAULT_PORT,
                    commandTerminator = terminator.replace("\\r", "\r").replace("\\n", "\n")
                )
                c.onCommandSent = { cmd -> append(LogKind.TX, cmd) }
                c.onTrace = { message -> if (showTrace) append(LogKind.INFO, message) }
                // nessuna attesa lo riconosce: dopo un timeout, un BUSY durante l'incasso, l'AN di una chiusura dal
                // pannello, l'esito di un incasso il cui chiamante e' stato cancellato
                c.onOrphanFrame = { frame -> error("frame orfano (nessuna attesa lo riconosce): ${frame.raw}") }
                c.onDisconnected = { ex ->
                    connected = false
                    statusText = if (ex == null) "disconnesso" else "disconnesso: ${ex.message}"
                }
                c.connect()

                scope.launch {
                    c.frames.collect { frame ->
                        append(LogKind.RX, frame.raw)
                        frame.json?.let { lastResponse = it }
                    }
                }

                client = c
                connected = true
                statusText = "connesso a ${host.trim()}:${port.trim()}"
                ok(statusText)
            } catch (e: Exception) {
                connected = false
                statusText = "disconnesso"
                error("connessione fallita: ${e.message}")
            }
        }
    }

    fun disconnect() {
        currentJob?.cancel()
        client?.disconnect()
        client = null
        connected = false
        statusText = "disconnesso"
    }

    /** Annulla l'operazione in corso lato client: il client invia [ AN] alla macchina. */
    @Suppress("unused")
    fun cancelCurrent() {
        currentJob?.cancel()
        info("operazione annullata dal client (invio AN)")
    }

    // ---------------------------------------------------------------- esecuzione

    fun exec(what: String, body: suspend (PagAmicoClient) -> Unit) {
        val c = client
        if (c == null || !connected) {
            error("non connesso: premere Connetti nella scheda Connessione")
            return
        }
        currentJob = scope.launch {
            busy = true
            info(what)
            try {
                body(c)
            } catch (e: PagAmicoException) {
                error(e.message ?: "errore pagAmico")
            } catch (e: Exception) {
                error("${e::class.simpleName}: ${e.message}")
            } finally {
                busy = false
            }
        }
    }

    fun execResponse(what: String, body: suspend (PagAmicoClient) -> PagAmicoResponse) =
        exec(what) { c ->
            val r = body(c)
            lastResponse = r
            summarize(r)
        }

    // ---------------------------------------------------------------- immagini

    /**
     * Sceglie un'immagine dal disco e la manda al display: [ SF] la registra come logo
     * permanente, [ SI] la mostra solo fino al prossimo [ SR]. Nessuno dei due comandi
     * risponde, quindi l'esito si vede sul display del dispositivo, non nel log.
     */
    fun sendImage(logo: Boolean) {
        val file = pickImage() ?: return

        val bytes = runCatching { file.readBytes() }.getOrElse {
            error("lettura di ${file.name} fallita: ${it.message}")
            return
        }

        if (bytes.size > IMMAGINE_GRANDE) {
            info(
                "${file.name}: ${bytes.size} byte in un unico invio. " +
                    "Conviene ridimensionare l'immagine prima di provarla sulla macchina vera."
            )
        }

        exec(if (logo) "[SF] invio logo" else "[SI] invio immagine temporanea") { c ->
            if (logo) c.sendLogo(bytes) else c.sendTemporaryImage(bytes)
        }
    }

    private fun pickImage(): File? {
        val dialog = FileDialog(null as Frame?, "Scegliere l'immagine da inviare", FileDialog.LOAD)
        dialog.file = "*.png;*.jpg;*.jpeg;*.bmp"     // su Windows AWT ignora setFilenameFilter
        dialog.isVisible = true

        val directory = dialog.directory ?: return null
        val name = dialog.file ?: return null
        return File(directory, name)
    }

    fun summarize(r: PagAmicoResponse) {
        val parts = mutableListOf("response=${r.response}")
        r.collectedAmount?.takeIf { it.signum() > 0 }?.let { parts += "incassato=$it" }
        r.changeCoins?.takeIf { it.signum() > 0 }?.let { parts += "restoMonete=$it" }
        r.changeBanknotes?.takeIf { it.signum() > 0 }?.let { parts += "restoBanconote=$it" }
        r.amountUnpaid?.takeIf { it.signum() > 0 }?.let { parts += "NON EROGATO=$it" }
        r.changeReturn?.takeIf { it.signum() > 0 }?.let { parts += "restituito=$it" }
        // esito di un CM: l'importo trattenuto e' collectedAmount, committedAmount e' solo il controllo
        if (r.response == "CM" && r.errorCode != "OK" && r.errorCode != "NO") {
            parts += "trattenuto=${r.collectedAmount} (controllo committedAmount=${r.committedAmount})"
        }
        r.amountBanknotesInBta?.takeIf { it.signum() > 0 }?.let { parts += "inBTA=$it" }
        r.noteCollectedBta?.takeIf { it > 0 }?.let { parts += "noteCollectedBTA=$it" }
        r.id?.let { parts += "Id=$it" }
        r.errorCode?.takeIf { it.isNotEmpty() }?.let { parts += "errorCode=$it (${PagAmicoErrorCodes.describe(it)})" }
        r.errorType?.takeIf { it.isNotEmpty() }?.let { parts += "errorType=$it" }
        ok(parts.joinToString("  "))

        r.status.warnings().forEach { error("stato macchina [${r.errorList}]: $it") }
    }

    fun dumpStock() {
        val r = lastResponse
        if (r == null) {
            error("nessuna risposta ricevuta: inviare prima [ST]")
            return
        }

        info("modello ${r.typePagAmico}  FW ${r.firmwareVersion}  matricola ${r.serialNumber}  errorList ${r.errorList}")
        info("monete disponibili   : " + fmtCoins(r.coinsByDenomination))
        info("soglie minime monete : " + fmtCoins(r.coinLimitsByDenomination))
        info("fondo cassa monete   : " + fmtCoins(r.coinsInStockByDenomination))
        info("banconote riciclatori: " + fmtNotes(r.banknotesAvailableByDenomination))
        info("fondo cassa banconote: " + fmtNotes(r.bankNotesInStockByDenomination))
        info("banconote in BTA     : " + fmtNotes(r.bankNotesBtaByDenomination))
        r.drawers.forEach { d ->
            info(if (d.isConfigured) "  $d" else "  cassetto#${d.index} non configurato")
        }
    }

    private fun fmtCoins(map: Map<Int, Int>) =
        if (map.isEmpty()) "(vuoto)"
        else map.toSortedMap().entries.joinToString("  ") { "%.2f=%d".format(it.key / 100.0, it.value) }

    private fun fmtNotes(map: Map<Int, Int>) =
        if (map.isEmpty()) "(vuoto)"
        else map.toSortedMap().entries.joinToString("  ") { "${it.key}EUR=${it.value}" }
}
