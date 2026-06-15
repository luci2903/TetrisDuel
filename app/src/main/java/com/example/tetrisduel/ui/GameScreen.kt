package com.example.tetrisduel.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tetrisduel.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    onNavigateToResult: (String, Int, Int, Int) -> Unit = { _, _, _, _ -> }
) {

    val piece by gameViewModel.currentPiece.collectAsState()
    val nextPiece by gameViewModel.nextPiece.collectAsState()
    val board by gameViewModel.board.collectAsState()
    val score by gameViewModel.score.collectAsState()
    val lines by gameViewModel.linesCleared.collectAsState()
    val elapsedTime by gameViewModel.elapsedTime.collectAsState()
    val gameOver by gameViewModel.gameOver.collectAsState()
    val victory by gameViewModel.victory.collectAsState()
    val opponentDisconnected by gameViewModel.opponentDisconnected.collectAsState()
    val flash37 by gameViewModel.flash37.collectAsState()

    LaunchedEffect(Unit) {
        while (!gameOver && !victory && !opponentDisconnected) {
            delay(1000)
            gameViewModel.moveDown()
        }
    }

    LaunchedEffect(gameOver, victory, opponentDisconnected) {
        if (gameOver) {
            delay(1500)
            onNavigateToResult("Opponent", score, lines, elapsedTime)
        } else if (victory) {
            delay(1500)
            onNavigateToResult("You", score, lines, elapsedTime)
        } else if (opponentDisconnected) {
            delay(1500)
            onNavigateToResult("You (Opponent disconnected)", score, lines, elapsedTime)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(text = "Puntaje: $score", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "Líneas: $lines", fontSize = 16.sp)
                val minutes = elapsedTime / 60
                val seconds = elapsedTime % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                Text(text = "Tiempo: $timeString", fontSize = 16.sp)
                if (opponentDisconnected) {
                    Text(text = "Oponente desconectado", color = Color.Red, fontSize = 14.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Siguiente", fontSize = 14.sp)
                Canvas(modifier = Modifier.size(80.dp)) {
                    val cellW = size.width / 4
                    val cellH = size.height / 4
                    nextPiece.shape.forEachIndexed { row, shapeRow ->
                        shapeRow.forEachIndexed { col, value ->
                            if (value == 1) {
                                drawRect(
                                    color = Color.Cyan,
                                    topLeft = Offset(col * cellW, row * cellH),
                                    size = Size(cellW, cellH)
                                )
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(col * cellW, row * cellH),
                                    size = Size(cellW, cellH),
                                    style = Stroke(2f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gameOver || victory) {
            Text(
                text = if (victory) "¡VICTORIA!" else "GAME OVER",
                color = if (victory) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Canvas(
            modifier = Modifier.size(
                width = 250.dp,
                height = 500.dp
            )
        ) {

            val cellWidth = size.width / 10
            val cellHeight = size.height / 20

            if (flash37) {

                drawRect(color = Color.Magenta.copy(alpha = 0.3f), topLeft = Offset(0f, 0f), size = size)
            }

            // TABLERO
            for (row in 0 until 20) {
                for (col in 0 until 10) {
                    val color = when (board[row][col]) {
                        1 -> Color.Blue
                        2 -> Color.Gray
                        else -> Color.DarkGray
                    }

                    drawRect(
                        color = color,
                        topLeft = Offset(col * cellWidth, row * cellHeight),
                        size = Size(cellWidth, cellHeight)
                    )

                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellWidth, row * cellHeight),
                        size = Size(cellWidth, cellHeight),
                        style = Stroke(1f)
                    )
                }
            }

            piece.shape.forEachIndexed { row, shapeRow ->
                shapeRow.forEachIndexed { col, value ->
                    if (value == 1) {
                        drawRect(
                            color = Color.Cyan,
                            topLeft = Offset(
                                (piece.x + col) * cellWidth,
                                (piece.y + row) * cellHeight
                            ),
                            size = Size(cellWidth, cellHeight)
                        )

                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(
                                (piece.x + col) * cellWidth,
                                (piece.y + row) * cellHeight
                            ),
                            size = Size(cellWidth, cellHeight),
                            style = Stroke(2f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { gameViewModel.moveLeft() }, enabled = !gameOver && !victory) { Text("⬅") }
            Button(onClick = { gameViewModel.fastDrop() }, enabled = !gameOver && !victory) { Text("⬇") }
            Button(onClick = { gameViewModel.rotatePiece() }, enabled = !gameOver && !victory) { Text("🔄") }
            Button(onClick = { gameViewModel.moveRight() }, enabled = !gameOver && !victory) { Text("➡") }
        }
        
        if (flash37) {
            Text("¡RULE 37!", color = Color.Magenta, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}