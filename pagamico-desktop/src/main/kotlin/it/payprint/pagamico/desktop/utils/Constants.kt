package it.payprint.pagamico.desktop.utils

import java.time.format.DateTimeFormatter

/** Oltre questa soglia l'invio di un'immagine merita un avviso: il dispositivo legge il socket in un colpo solo. */
const val IMMAGINE_GRANDE = 512 * 1024

val FILE_STAMP: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")