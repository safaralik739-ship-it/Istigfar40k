package com.istighfar.app40k.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.istighfar.app40k.data.local.HistoryEntry
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExporter {

    fun exportToCsvAndShare(context: Context, entries: List<HistoryEntry>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "istighfar_export_${System.currentTimeMillis()}.csv")

        FileWriter(file).use { writer ->
            writer.append("Дата,Время,Изменение,Итоговый счёт,Тип\n")
            entries.sortedBy { it.timestamp }.forEach { entry ->
                val dateStr = sdf.format(Date(entry.timestamp))
                writer.append("$dateStr,${entry.amount},${entry.resultingTotal},${entry.type}\n")
            }
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Экспорт данных Istighfar 40K"))
    }
}
