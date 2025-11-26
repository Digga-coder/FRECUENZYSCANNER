package com.frecuenzy.scanner.domain.models
import com.frecuenzy.scanner.data.local.entities.Participante
import com.frecuenzy.scanner.data.local.entities.Ticket
sealed class ValidationResult {
    data class Valid(val ticket: Ticket, val participante: Participante) : ValidationResult()
    data class Duplicate(val ticket: Ticket, val participante: Participante, val lecturas: Int) : ValidationResult()
    object NotFound : ValidationResult()
}