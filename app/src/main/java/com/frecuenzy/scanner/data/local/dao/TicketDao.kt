package com.frecuenzy.scanner.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frecuenzy.scanner.data.local.entities.Ticket
import com.frecuenzy.scanner.data.local.entities.TicketConParticipante

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets WHERE ticket_code = :ticketCode LIMIT 1")
    suspend fun getTicketByCode(ticketCode: String): Ticket?
    
    @Transaction
    @Query("SELECT * FROM tickets WHERE ticket_code = :ticketCode LIMIT 1")
    suspend fun getTicketConParticipanteByCode(ticketCode: String): TicketConParticipante?
    
    @Update
    suspend fun updateTicket(ticket: Ticket)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<Ticket>)
    
    @Query("DELETE FROM tickets")
    suspend fun deleteAllTickets()
    
    @Query("SELECT COUNT(*) FROM tickets")
    suspend fun countTickets(): Int
    
    @Query("SELECT COUNT(*) FROM tickets WHERE estado = 'validado'")
    suspend fun countValidados(): Int
}