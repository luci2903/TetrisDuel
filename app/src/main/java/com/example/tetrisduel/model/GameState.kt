package com.example.tetrisduel.model

data class GameState(

    val board: Array<IntArray> =
        Array(20) { IntArray(10) },

    val score: Int = 0,

    val lines: Int = 0,

    val gameOver: Boolean = false
)