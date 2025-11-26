package com.frecuenzy.scanner.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.frecuenzy.scanner.data.local.entities.Participante

@Dao
interface ParticipanteDao {
    @Query("SELECT * FROM participantes WHERE id = :id LIMIT 1")
    suspend fun getParticipanteById(id: String): Participante?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipantes(participantes: List<Participante>)
    
    @Query("DELETE FROM participantes")
    suspend fun deleteAllParticipantes()
}