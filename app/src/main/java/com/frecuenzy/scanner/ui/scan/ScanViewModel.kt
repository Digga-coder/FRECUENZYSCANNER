package com.frecuenzy.scanner.ui.scan

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.frecuenzy.scanner.data.local.AppDatabase
import com.frecuenzy.scanner.data.repository.TicketRepository
import com.frecuenzy.scanner.domain.models.ValidationResult
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = TicketRepository(database)
    var scanResult by mutableStateOf<ValidationResult?>(null)
        private set
        
    fun validateTicket(ticketCode: String) {
        if (scanResult != null) return 
        viewModelScope.launch { scanResult = repository.validateTicket(ticketCode, "Operador 1") }
    }
    fun dismissResult() { scanResult = null }
}