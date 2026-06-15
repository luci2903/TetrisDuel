package com.example.tetrisduel.repository

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject

object SocketRepository {

    private val SERVER_URL = "http://10.0.2.2:3000"
    
    private val options = IO.Options().apply {
        forceNew = true
        reconnection = true
        transports = arrayOf("websocket")
    }

    private var socket: Socket = IO.socket(SERVER_URL, options)

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _roomCode = MutableStateFlow("")
    val roomCode = _roomCode.asStateFlow()

    private val _gameStarted = MutableStateFlow(false)
    val gameStarted = _gameStarted.asStateFlow()

    private val _attackEvent = MutableSharedFlow<Int>(extraBufferCapacity = 10)
    val attackEvent = _attackEvent.asSharedFlow()

    private val _opponentDisconnected = MutableStateFlow(false)
    val opponentDisconnected = _opponentDisconnected.asStateFlow()

    private val _victory = MutableStateFlow(false)
    val victory = _victory.asStateFlow()

    fun connect() {
        if (!socket.connected()) {
            setupListeners()
            socket.connect()
        }
    }

    fun disconnect() {
        socket.disconnect()
    }

    private fun setupListeners() {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("SOCKET", "CONECTADO al servidor")
            _isConnected.value = true
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e("SOCKET", "ERROR DE CONEXIÓN: ${args[0]}")
            _isConnected.value = false
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d("SOCKET", "DESCONECTADO del servidor")
            _isConnected.value = false
            _gameStarted.value = false
        }

        socket.on("room_created") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val roomId = data.getString("roomId")
                Log.d("SOCKET", "Sala Creada: $roomId")
                _roomCode.value = roomId
            }
        }

        socket.on("game_start") {
            Log.d("SOCKET", "PARTIDA INICIADA")
            _gameStarted.value = true
        }

        socket.on("receive_attack") { args ->
            try {
                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject
                    val lines = data.getInt("garbageLines")
                    _attackEvent.tryEmit(lines)
                }
            } catch (e: Exception) {
                Log.e("SOCKET", "Error parseando receive_attack", e)
            }
        }

        socket.on("opponent_disconnected") {
            _opponentDisconnected.value = true
        }

        socket.on("victory") {
            _victory.value = true
        }
    }

    fun createRoom() {
        socket.emit("create_room")
    }

    fun joinRoom(roomId: String) {
        val payload = JSONObject().apply { put("roomId", roomId) }
        _roomCode.value = roomId
        socket.emit("join_room", payload)
    }

    fun sendAttack(lines: Int) {
        if (roomCode.value.isNotEmpty()) {
            val payload = JSONObject().apply {
                put("roomId", roomCode.value)
                put("garbageLines", lines)
            }
            socket.emit("send_attack", payload)
        }
    }

    fun sendGameOver() {
        if (roomCode.value.isNotEmpty()) {
            val payload = JSONObject().apply { put("roomId", roomCode.value) }
            socket.emit("game_over", payload)
        }
    }
}
