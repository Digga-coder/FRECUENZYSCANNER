package com.frecuenzy.scanner.data.local.entities
import androidx.room.Embedded
import androidx.room.Relation

data class TicketConParticipante(
    @Embedded val ticket: Ticket,
    @Relation(
        parentColumn = "participante_id",
        entityColumn = "id"
    )
    val participante: Participante
)