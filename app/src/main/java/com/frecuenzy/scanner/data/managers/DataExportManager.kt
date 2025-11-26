package com.frecuenzy.scanner.data.managers

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.frecuenzy.scanner.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataExportManager(
    private val database: AppDatabase,
    private val context: Context
) {
    suspend fun exportLogsToCSV(): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val logs = database.scanLogDao().getAllLogs()
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "frecuenzy_logs_$timestamp.csv"
                
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                
                file.bufferedWriter().use { writer ->
                    writer.write("ticket_code,timestamp,fecha_hora,resultado,operador\n")
                    logs.forEach { log ->
                        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        writer.write("${log.ticketCode},${log.timestamp},$dateTime,${log.resultado},${log.operador ?: "N/A"}\n")
                    }
                }
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } catch (e: Exception) {
                null
            }
        }
    }
}