package com.example.tetrisduel.game

import com.example.tetrisduel.model.*

object PieceGenerator {

    fun nextPiece(): Tetromino {

        return TetrominoFactory.create(
            TetrominoType.entries.random()
        )
    }
}