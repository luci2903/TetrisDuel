package com.example.tetrisduel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lobby") {
        composable("lobby") {
            LobbyScreen(
                onNavigateToGame = {
                    navController.navigate("game") {
                        popUpTo("lobby") { inclusive = true }
                    }
                }
            )
        }
        composable("game") {
            GameScreen(
                onNavigateToResult = { winner, score, lines, duration ->
                    navController.navigate("result/$winner/$score/$lines/$duration") {
                        popUpTo("game") { inclusive = true }
                    }
                }
            )
        }
        composable("result/{winner}/{score}/{lines}/{duration}") { backStackEntry ->
            val winner = backStackEntry.arguments?.getString("winner") ?: "Unknown"
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val lines = backStackEntry.arguments?.getString("lines")?.toIntOrNull() ?: 0
            val duration = backStackEntry.arguments?.getString("duration")?.toIntOrNull() ?: 0

            ResultScreen(
                winner = winner,
                score = score,
                lines = lines,
                duration = duration,
                onPlayAgain = {
                    navController.navigate("lobby") {
                        popUpTo("result") { inclusive = true }
                    }
                }
            )
        }
    }
}
