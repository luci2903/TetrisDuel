package com.example.tetrisduel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tetrisduel.game.PieceGenerator
import com.example.tetrisduel.repository.SocketRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val socketRepository = SocketRepository


    private val _board = MutableStateFlow(Array(20) { IntArray(10) })
    val board = _board.asStateFlow()

    private val _gameOver = MutableStateFlow(false)
    val gameOver = _gameOver.asStateFlow()

    private val _currentPiece = MutableStateFlow(PieceGenerator.nextPiece())
    val currentPiece = _currentPiece.asStateFlow()

    private val _nextPiece = MutableStateFlow(PieceGenerator.nextPiece())
    val nextPiece = _nextPiece.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _linesCleared = MutableStateFlow(0)
    val linesCleared = _linesCleared.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _flash37 = MutableStateFlow(false)
    val flash37 = _flash37.asStateFlow()

    val victory = socketRepository.victory
    val opponentDisconnected = socketRepository.opponentDisconnected

    init {
        viewModelScope.launch {
            socketRepository.attackEvent.collect { lines ->
                if (lines > 0) {
                    addGarbageLines(lines)
                }
            }
        }

        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_gameOver.value && !victory.value && !opponentDisconnected.value) {
                    _elapsedTime.value += 1
                }
            }
        }
    }

    fun moveDown() {
        if (_gameOver.value || victory.value) return
        val piece = _currentPiece.value

        if (canMove(piece.x, piece.y + 1, piece.shape)) {
            _currentPiece.value = piece.copy(y = piece.y + 1)
        } else {
            lockPiece()
            clearLines()
            generateNewPiece()
        }
    }

    fun moveLeft() {
        if (_gameOver.value || victory.value) return
        val piece = _currentPiece.value
        if (canMove(piece.x - 1, piece.y, piece.shape)) {
            _currentPiece.value = piece.copy(x = piece.x - 1)
        }
    }

    fun moveRight() {
        if (_gameOver.value || victory.value) return
        val piece = _currentPiece.value
        if (canMove(piece.x + 1, piece.y, piece.shape)) {
            _currentPiece.value = piece.copy(x = piece.x + 1)
        }
    }

    fun fastDrop() {
        if (_gameOver.value || victory.value) return
        var y = _currentPiece.value.y
        while (canMove(_currentPiece.value.x, y + 1, _currentPiece.value.shape)) {
            y++
        }
        _currentPiece.value = _currentPiece.value.copy(y = y)
        lockPiece()
        clearLines()
        generateNewPiece()
    }

    fun rotatePiece() {
        if (_gameOver.value || victory.value) return
        val piece = _currentPiece.value
        val rotated = piece.shape[0].indices.map { col ->
            piece.shape.indices.reversed().map { row ->
                piece.shape[row][col]
            }
        }

        if (canMove(piece.x, piece.y, rotated)) {
            _currentPiece.value = piece.copy(shape = rotated)
        }
    }

    private fun lockPiece() {
        val piece = _currentPiece.value
        val boardCopy = _board.value.map { it.clone() }.toTypedArray()

        piece.shape.forEachIndexed { row, shapeRow ->
            shapeRow.forEachIndexed { col, value ->
                if (value == 1) {
                    val boardRow = piece.y + row
                    val boardCol = piece.x + col
                    if (boardRow in 0 until 20 && boardCol in 0 until 10) {
                        boardCopy[boardRow][boardCol] = 1
                    }
                }
            }
        }
        _board.value = boardCopy
    }

    private fun canMove(newX: Int, newY: Int, shape: List<List<Int>>): Boolean {
        val currentBoard = _board.value
        shape.forEachIndexed { row, shapeRow ->
            shapeRow.forEachIndexed { col, value ->
                if (value == 1) {
                    val boardRow = newY + row
                    val boardCol = newX + col
                    if (boardCol < 0 || boardCol >= 10 || boardRow >= 20) return false
                    if (boardRow >= 0 && currentBoard[boardRow][boardCol] != 0) return false
                }
            }
        }
        return true
    }

    private fun clearLines() {
        val currentBoard = _board.value
        val newBoard = mutableListOf<IntArray>()
        var removedLines = 0

        for (row in currentBoard) {
            if (row.all { it != 0 }) {
                removedLines++
            } else {
                newBoard.add(row)
            }
        }

        repeat(removedLines) {
            newBoard.add(0, IntArray(10))
        }

        _board.value = newBoard.toTypedArray()

        if (removedLines > 0) {
            _linesCleared.value += removedLines
            _score.value += when (removedLines) {
                1 -> 100
                2 -> 300
                3 -> 500
                4 -> 800
                else -> 0
            }

            if (removedLines >= 3) {
                viewModelScope.launch {
                    _flash37.value = true
                    delay(1000)
                    _flash37.value = false
                }
            }

            val garbageLines = when(removedLines) {
                1 -> 0
                2 -> 1
                3 -> 2
                4 -> 4
                else -> 0
            }

            if (garbageLines > 0) {
                socketRepository.sendAttack(garbageLines)
            }
        }
    }

    private fun addGarbageLines(lines: Int) {
        val currentBoard = _board.value
        val newBoard = Array(20) { IntArray(10) }
        
        // Desplazar hacia arriba
        for (i in 0 until 20 - lines) {
            newBoard[i] = currentBoard[i + lines]
        }
        
        // Agregar líneas basura
        for (i in 20 - lines until 20) {
            val emptyHole = (0 until 10).random()
            for (j in 0 until 10) {
                newBoard[i][j] = if (j == emptyHole) 0 else 2 // 2 representa bloque gris/basura
            }
        }
        
        _board.value = newBoard
        
        // Si al desplazar hacia arriba hay bloques en fila < 0, sería game over (simplificado)
        if (currentBoard.take(lines).any { row -> row.any { it != 0 } }) {
            _gameOver.value = true
            socketRepository.sendGameOver()
        }
    }

    private fun generateNewPiece() {
        val newPiece = _nextPiece.value
        _nextPiece.value = PieceGenerator.nextPiece()

        if (!canMove(newPiece.x, newPiece.y, newPiece.shape)) {
            _gameOver.value = true
            socketRepository.sendGameOver()
            return
        }
        _currentPiece.value = newPiece
    }

    override fun onCleared() {
        super.onCleared()
        socketRepository.disconnect()
    }
}