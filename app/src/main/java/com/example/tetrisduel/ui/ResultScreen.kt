package com.example.tetrisduel.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(
    winner: String,
    score: Int,
    lines: Int,
    duration: Int,
    onPlayAgain: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val color = if (winner == "You") Color(0xFF4CAF50) else Color(0xFFF44336)
        
        Text(
            text = if (winner == "You") "¡VICTORIA!" else "DERROTA",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "Puntaje: $score", fontSize = 24.sp)
        Text(text = "Líneas: $lines", fontSize = 24.sp)
        
        val minutes = duration / 60
        val seconds = duration % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)
        Text(text = "Duración: $timeString", fontSize = 24.sp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onPlayAgain) {
            Text("Volver al Lobby")
        }
    }
}
