package com.example.tetrisduel.model

data class Board(
    val rows: Int = 20,
    val cols: Int = 10,
    val grid: Array<IntArray> =
        Array(20) { IntArray(10) }
)