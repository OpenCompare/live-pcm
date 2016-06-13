var socket = new WebSocket("ws://localhost:9000/stream");

socket.onmessage = function (event) {
    console.log(event.data);
};
