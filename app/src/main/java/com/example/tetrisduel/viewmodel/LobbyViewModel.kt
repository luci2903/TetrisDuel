package com.example.tetrisduel.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tetrisduel.repository.SocketRepository
import kotlinx.coroutines.flow.StateFlow

class LobbyViewModel : ViewModel() {

    private val socketRepository = SocketRepository

    val roomCode: StateFlow<String> = socketRepository.roomCode
    val isConnected: StateFlow<Boolean> = socketRepository.isConnected
    val gameStarted: StateFlow<Boolean> = socketRepository.gameStarted

    init {
        socketRepository.connect()
    }

    fun createRoom() {
        socketRepository.createRoom()
    }

    fun joinRoom(code: String) {
        socketRepository.joinRoom(code)
    }
}