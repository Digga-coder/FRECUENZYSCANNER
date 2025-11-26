package com.frecuenzy.scanner.data.managers

import android.content.Context
import android.net.Uri
import com.frecuenzy.scanner.data.local.AppDatabase
import com.frecuenzy.scanner.data.local.entities.Participante
import com.frecuenzy.scanner.data.local.entities.Ticket
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataImportManager(
    private val database: AppDatabase,
    private val context: Context
) {
    suspend fun importFromJson(uri: Uri): ImportResult {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use {
                    it.bufferedReader().readText()
                }
                if (jsonString == null) {
                    return@withContext ImportResult.Error("No se pudo leer el archivo")
                }
                
                val gson = Gson()
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                val recordsArray = jsonObject.getAsJsonArray("records")
                
                val participantesMap = mutableMapOf<String, Participante>()
                val tickets = mutableListOf<Ticket>()
                
                recordsArray.forEach { element ->
                    val obj = element.asJsonObject
                    val participanteId = obj.get("participante_id").asString
                    
                    if (!participantesMap.containsKey(participanteId)) {
                        participantesMap[participanteId] = Participante(
                            id = participanteId,
                            pedidoId = obj.get("pedido_id").asString,
                            email = obj.get("email").asString,
                            telefono = if (obj.has("telefono") && !obj.get("telefono").isJsonNull) obj.get("telefono").asString else null,
                            tipoEntrada = obj.get("tipo_entrada").asString,
                            createdAt = obj.get("participante_created_at").asString
                        )
                    }
                    tickets.add(
                        Ticket(
                            id = obj.get("ticket_id").asString,
                            participanteId = participanteId,
                            pedidoId = obj.get("pedido_id").asString,
                            ticketCode = obj.get("ticket_code").asString,
                            qrUrl = obj.get("qr_url").asString,
                            sentEmail = obj.get("sent_email").asBoolean,
                            sentSms = obj.get("sent_sms").asBoolean,
                            createdAt = obj.get("ticket_created_at").asString
                        )
                    )
                }
                database.participanteDao().insertParticipantes(participantesMap.values.toList())
                database.ticketDao().insertTickets(tickets)
                ImportResult.Success(participantesMap.size, tickets.size)
            } catch (e: Exception) {
                ImportResult.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
sealed class ImportResult {
    data class Success(val participantes: Int, val tickets: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
}