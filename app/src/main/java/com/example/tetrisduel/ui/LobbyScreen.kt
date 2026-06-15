package com.example.tetrisduel.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tetrisduel.viewmodel.LobbyViewModel

@Composable
fun LobbyScreen(
    lobbyViewModel: LobbyViewModel = viewModel(),
    onNavigateToGame: () -> Unit = {}
) {
    val roomCode by lobbyViewModel.roomCode.collectAsState()
    val isConnected by lobbyViewModel.isConnected.collectAsState()
    val gameStarted by lobbyViewModel.gameStarted.collectAsState()
    var inputCode by remember { mutableStateOf("") }

    LaunchedEffect(gameStarted) {
        if (gameStarted) {
            onNavigateToGame()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isConnected) "Conectado al servidor" else "Conectando...", color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { lobbyViewModel.createRoom() }, enabled = isConnected) {
            Text("Crear Sala")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (roomCode.isNotEmpty()) {
            Text(text = "Código de la sala: $roomCode", style = MaterialTheme.typography.titleLarge)
            Text(text = "Esperando al oponente...", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider()
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = inputCode,
            onValueChange = { inputCode = it.uppercase() },
            label = { Text("Código para unirse") },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { lobbyViewModel.joinRoom(inputCode) },
            enabled = isConnected && inputCode.length >= 6
        ) {
            Text("Unirse a Sala")
        }
    }
}