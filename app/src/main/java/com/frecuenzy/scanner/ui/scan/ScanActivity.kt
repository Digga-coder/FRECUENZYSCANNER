package com.frecuenzy.scanner.ui.scan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.frecuenzy.scanner.data.local.entities.Participante
import com.frecuenzy.scanner.data.local.entities.Ticket
import com.frecuenzy.scanner.domain.models.ValidationResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

class ScanActivity : ComponentActivity() {
    private val viewModel: ScanViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    ScanScreen(scanResult = viewModel.scanResult, onQrScanned = { code -> viewModel.validateTicket(code) }, onDismiss = { viewModel.dismissResult() })
                }
            }
        }
    }
}

@Composable
fun ScanScreen(scanResult: ValidationResult?, onQrScanned: (String) -> Unit, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context -> CompoundBarcodeView(context).apply { decodeContinuous { result -> onQrScanned(result.text) }; resume() } }, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val frameSize = size.minDimension * 0.7f
                drawRect(color = Color.White, topLeft = Offset((size.width - frameSize) / 2, (size.height - frameSize) / 2), size = Size(frameSize, frameSize), style = Stroke(width = 4.dp.toPx()))
            }
            Text(text = "Enfoca el código QR", color = Color.White, fontSize = 18.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp))
        }
        scanResult?.let { result -> ResultOverlay(result = result, onContinue = onDismiss) }
    }
}

@Composable
fun ResultOverlay(result: ValidationResult, onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp), elevation = CardDefaults.cardElevation(8.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                when (result) {
                    is ValidationResult.Valid -> {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("✓ ACCESO VÁLIDO", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        ParticipantInfo(result.participante, result.ticket)
                    }
                    is ValidationResult.Duplicate -> {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFFF9800), modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("⚠ QR YA LEÍDO", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                        Text("Lecturas: ${result.lecturas}", style = MaterialTheme.typography.titleLarge, color = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.height(24.dp))
                        ParticipantInfo(result.participante, result.ticket)
                    }
                    is ValidationResult.NotFound -> {
                        Icon(Icons.Default.Error, null, tint = Color(0xFFF44336), modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("✗ TICKET NO ENCONTRADO", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFF44336), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Continuar Escaneando", fontSize = 16.sp) }
            }
        }
    }
}

@Composable
fun ParticipantInfo(participante: Participante, ticket: Ticket) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoRow("Email:", participante.email)
        InfoRow("Teléfono:", participante.telefono ?: "N/A")
        InfoRow("Tipo Entrada:", participante.tipoEntrada)
        InfoRow("Código Ticket:", ticket.ticketCode)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}