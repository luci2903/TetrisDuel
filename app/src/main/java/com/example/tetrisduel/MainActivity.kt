package com.example.tetrisduel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tetrisduel.ui.AppNavigation
import com.example.tetrisduel.ui.theme.TetrisDuelTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TetrisDuelTheme {
                AppNavigation()
            }
        }
    }
}