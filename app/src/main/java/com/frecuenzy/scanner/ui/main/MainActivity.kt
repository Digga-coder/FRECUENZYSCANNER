package com.frecuenzy.scanner.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frecuenzy.scanner.domain.models.EventStats
import com.frecuenzy.scanner.ui.scan.ScanActivity

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val filePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
                        uri?.let { viewModel.importData(it) { result -> Toast.makeText(this, "Importación: $result", Toast.LENGTH_LONG).show() } }
                    }
                    
                    MainScreen(
                        stats = viewModel.stats,
                        onScanClick = { startActivity(Intent(this, ScanActivity::class.java)) },
                        onImportClick = { filePickerLauncher.launch(arrayOf("application/json")) },
                        onExportClick = {
                            viewModel.exportData { uri ->
                                if (uri != null) {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        type = "text/csv"
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    startActivity(Intent.createChooser(shareIntent, "Compartir Logs"))
                                } else {
                                    Toast.makeText(this, "Error al exportar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onClearDataClick = {
                            viewModel.clearData()
                            Toast.makeText(this, "Datos borrados", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
    override fun onResume() { super.onResume(); viewModel.refreshStats() }
}

@Composable
fun MainScreen(stats: EventStats, onScanClick: () -> Unit, onImportClick: () -> Unit, onExportClick: () -> Unit, onClearDataClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Control de Acceso Frecuenzy", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatRow("Total Tickets:", stats.totalTickets.toString())
                StatRow("Validados:", stats.validados.toString())
                StatRow("Total Lecturas:", stats.totalScans.toString())
            }
        }
        Button(onClick = onScanClick, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("ESCANEAR CÓDIGO QR", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onImportClick, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Upload, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Importar Base de Datos") }
        OutlinedButton(onClick = onExportClick, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Download, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Exportar Logs de Acceso") }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onClearDataClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Icon(Icons.Default.Delete, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Borrar Todos los Datos") }
    }
}
@Composable
fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}