package com.frecuenzy.scanner.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "participantes")
data class Participante(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "pedido_id")
    val pedidoId: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "telefono")
    val telefono: String?,
    
    @ColumnInfo(name = "tipo_entrada")
    val tipoEntrada: String,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String
)