package com.frecuenzy.scanner.ui.main

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.frecuenzy.scanner.data.local.AppDatabase
import com.frecuenzy.scanner.data.managers.DataExportManager
import com.frecuenzy.scanner.data.managers.DataImportManager
import com.frecuenzy.scanner.data.repository.TicketRepository
import com.frecuenzy.scanner.domain.models.EventStats
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = TicketRepository(database)
    private val importManager = DataImportManager(database, application)
    private val exportManager = DataExportManager(database, application)
    
    var stats by mutableStateOf(EventStats(0, 0, 0))
        private set
        
    init { refreshStats() }
    
    fun refreshStats() {
        viewModelScope.launch { stats = repository.getStats() }
    }
    
    fun importData(uri: Uri, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = importManager.importFromJson(uri)
            refreshStats()
            onResult(result.toString())
        }
    }
    
    fun exportData(onResult: (Uri?) -> Unit) {
        viewModelScope.launch {
            val uri = exportManager.exportLogsToCSV()
            onResult(uri)
        }
    }
    
    fun clearData() {
        viewModelScope.launch {
            database.ticketDao().deleteAllTickets()
            database.participanteDao().deleteAllParticipantes()
            database.scanLogDao().deleteAllLogs()
            refreshStats()
        }
    }
}