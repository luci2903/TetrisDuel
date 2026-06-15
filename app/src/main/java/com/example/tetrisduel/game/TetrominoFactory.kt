package com.example.tetrisduel.game

import com.example.tetrisduel.model.*

object TetrominoFactory {

    fun create(type: TetrominoType): Tetromino {

        return when (type) {

            TetrominoType.I -> Tetromino(
                type,
                listOf(
                    listOf(1, 1, 1, 1)
                ),
                3,
                0
            )

            TetrominoType.O -> Tetromino(
                type,
                listOf(
                    listOf(1, 1),
                    listOf(1, 1)
                ),
                4,
                0
            )

            TetrominoType.T -> Tetromino(
                type,
                listOf(
                    listOf(0, 1, 0),
                    listOf(1, 1, 1)
                ),
                3,
                0
            )

            TetrominoType.S -> Tetromino(
                type,
                listOf(
                    listOf(0, 1, 1),
                    listOf(1, 1, 0)
                ),
                3,
                0
            )

            TetrominoType.Z -> Tetromino(
                type,
                listOf(
                    listOf(1, 1, 0),
                    listOf(0, 1, 1)
                ),
                3,
                0
            )

            TetrominoType.J -> Tetromino(
                type,
                listOf(
                    listOf(1, 0, 0),
                    listOf(1, 1, 1)
                ),
                3,
                0
            )

            TetrominoType.L -> Tetromino(
                type,
                listOf(
                    listOf(0, 0, 1),
                    listOf(1, 1, 1)
                ),
                3,
                0
            )
        }
    }
}