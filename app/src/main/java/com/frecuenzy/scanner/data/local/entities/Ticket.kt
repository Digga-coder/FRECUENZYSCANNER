package com.frecuenzy.scanner.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = Participante::class,
            parentColumns = ["id"],
            childColumns = ["participante_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ticket_code"], unique = true)]
)
data class Ticket(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "participante_id")
    val participanteId: String,
    
    @ColumnInfo(name = "pedido_id")
    val pedidoId: String,
    
    @ColumnInfo(name = "ticket_code")
    val ticketCode: String,
    
    @ColumnInfo(name = "qr_url")
    val qrUrl: String,
    
    @ColumnInfo(name = "sent_email")
    val sentEmail: Boolean,
    
    @ColumnInfo(name = "sent_sms")
    val sentSms: Boolean,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "estado")
    val estado: String = "pendiente",
    
    @ColumnInfo(name = "n_lecturas")
    val nLecturas: Int = 0,
    
    @ColumnInfo(name = "asistencia_marcada")
    val asistenciaMarcada: Boolean = false
)