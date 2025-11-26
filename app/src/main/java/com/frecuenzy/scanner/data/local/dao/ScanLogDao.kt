package com.frecuenzy.scanner.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.frecuenzy.scanner.data.local.entities.ScanLog

@Dao
interface ScanLogDao {
    @Insert
    suspend fun insertLog(log: ScanLog): Long
    
    @Query("SELECT * FROM scan_logs ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<ScanLog>
    
    @Query("SELECT * FROM scan_logs WHERE ticket_code = :ticketCode ORDER BY timestamp DESC")
    suspend fun getLogsByTicketCode(ticketCode: String): List<ScanLog>
    
    @Query("DELETE FROM scan_logs")
    suspend fun deleteAllLogs()
}