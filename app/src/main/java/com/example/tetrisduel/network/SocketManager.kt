package com.example.tetrisduel.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

object SocketManager {

    private const val SERVER_URL = "http://192.168.0.13:3000"
    val socket: Socket by lazy {

        val options = IO.Options()

        options.forceNew = true
        options.reconnection = true
        options.transports = arrayOf("websocket")

        IO.socket(SERVER_URL, options)
    }

    fun connect() {

        socket.on(Socket.EVENT_CONNECT) {
            Log.d("SOCKET", "CONECTADO")
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e("SOCKET", "ERROR: ${args[0]}")
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d("SOCKET", "DESCONECTADO")
        }
        socket.connect()


    }
    fun createRoom() {
        android.util.Log.d("SOCKET", "ENVIANDO CREATE_ROOM")
        socket.emit("create_room")
    }
}