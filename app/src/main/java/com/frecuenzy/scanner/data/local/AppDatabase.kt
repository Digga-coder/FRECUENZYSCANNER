package com.frecuenzy.scanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.frecuenzy.scanner.data.local.dao.ParticipanteDao
import com.frecuenzy.scanner.data.local.dao.ScanLogDao
import com.frecuenzy.scanner.data.local.dao.TicketDao
import com.frecuenzy.scanner.data.local.entities.Participante
import com.frecuenzy.scanner.data.local.entities.ScanLog
import com.frecuenzy.scanner.data.local.entities.Ticket
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Ticket::class, Participante::class, ScanLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun ticketDao(): TicketDao
    abstract fun participanteDao(): ParticipanteDao
    abstract fun scanLogDao(): ScanLogDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Encryption key
                val passphrase = SQLiteDatabase.getBytes("frecuenzy_secure_2025".toCharArray())
                val factory = SupportFactory(passphrase)
                
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "frecuenzy_event_encrypted.db"
                )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}