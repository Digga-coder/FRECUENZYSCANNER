package com.frecuenzy.scanner.data.repository

import com.frecuenzy.scanner.data.local.AppDatabase
import com.frecuenzy.scanner.data.local.entities.ScanLog
import com.frecuenzy.scanner.domain.models.EventStats
import com.frecuenzy.scanner.domain.models.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TicketRepository(private val database: AppDatabase) {
    
    private val ticketDao = database.ticketDao()
    private val scanLogDao = database.scanLogDao()
    
    suspend fun validateTicket(ticketCode: String, operador: String?): ValidationResult {
        return withContext(Dispatchers.IO) {
            val ticketConParticipante = ticketDao.getTicketConParticipanteByCode(ticketCode)
            
            if (ticketConParticipante == null) {
                scanLogDao.insertLog(
                    ScanLog(ticketCode = ticketCode, timestamp = System.currentTimeMillis(), resultado = "no_encontrado", operador = operador)
                )
                return@withContext ValidationResult.NotFound
            }
            
            val ticket = ticketConParticipante.ticket
            val participante = ticketConParticipante.participante
            val isDuplicate = ticket.nLecturas > 0
            
            val updatedTicket = ticket.copy(
                nLecturas = ticket.nLecturas + 1,
                estado = if (!isDuplicate) "validado" else ticket.estado,
                asistenciaMarcada = true
            )
            ticketDao.updateTicket(updatedTicket)
            
            scanLogDao.insertLog(
                ScanLog(
                    ticketCode = ticketCode,
                    timestamp = System.currentTimeMillis(),
                    resultado = if (isDuplicate) "duplicado" else "valido",
                    operador = operador
                )
            )
            
            if (isDuplicate) {
                ValidationResult.Duplicate(updatedTicket, participante, ticket.nLecturas + 1)
            } else {
                ValidationResult.Valid(updatedTicket, participante)
            }
        }
    }
    
    suspend fun getStats(): EventStats {
        return withContext(Dispatchers.IO) {
            EventStats(
                totalTickets = ticketDao.countTickets(),
                validados = ticketDao.countValidados(),
                totalScans = scanLogDao.getAllLogs().size
            )
        }
    }
}