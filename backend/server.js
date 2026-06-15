const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const cors = require("cors");

const app = express();

app.use(cors());

const server = http.createServer(app);

const io = new Server(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

const rooms = {};

io.on("connection", (socket) => {

    console.log("Connected:", socket.id);

    socket.on("create_room", () => {

        console.log("CREATE_ROOM RECIBIDO");
        const roomId = Math.random()
            .toString(36)
            .substring(2, 8)
            .toUpperCase();

        rooms[roomId] = {
            players: [socket.id]
        };

        socket.join(roomId);

        socket.emit("room_created", {
            roomId
        });
    });

    socket.on("join_room", ({ roomId }) => {

        const room = rooms[roomId];

        if (!room) {

            socket.emit("error_message", {
                message: "Room not found"
            });

            return;
        }

        if (room.players.length >= 2) {

            socket.emit("error_message", {
                message: "Room full"
            });

            return;
        }

        room.players.push(socket.id);

        socket.join(roomId);

        io.to(roomId).emit("game_start");
    });

    socket.on("send_attack", ({ roomId, garbageLines }) => {

        socket.to(roomId).emit("receive_attack", {
            garbageLines
        });
    });

    socket.on("game_over", ({ roomId }) => {

        socket.to(roomId).emit("victory");
    });

    socket.on("disconnect", () => {

        for (const roomId in rooms) {

            const room = rooms[roomId];

            if (room.players.includes(socket.id)) {

                socket.to(roomId).emit("opponent_disconnected");

                delete rooms[roomId];
            }
        }
    });
});

app.get("/", (_, res) => {
    res.send("Tetris Duel Server Running");
});

server.listen(3000, "0.0.0.0", () => {
    console.log("Server running on port 3000");
});
