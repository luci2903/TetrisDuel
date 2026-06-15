package com.example.tetrisduel.model

data class Tetromino(
    val type: TetrominoType,
    var shape: List<List<Int>>,
    var x: Int,
    var y: Int
)