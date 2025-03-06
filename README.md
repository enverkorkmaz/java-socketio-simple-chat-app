# Simple Chat App with Socket.IO & Spring Boot

This is a simple chat application built with **Spring Boot** and **Socket.IO**. The main goal of this project was to learn how real-time communication works using WebSockets.

## Features
- **Real-time messaging** between clients.
- **Rooms support** (users can join different chat rooms).
- **Automatic room assignment** (default room is `general`).
- **User connection/disconnection events**.

## Technologies Used
- **Spring Boot** (Backend framework)
- **Socket.IO** (Real-time WebSocket communication)
- **Java 21** (Programming language)

## How to Run 🚀
1. Clone the repository:
   ```sh
   git clone https://github.com/enverkorkmaz/java-socketio-simple-chat-app.git
   cd java-socketio-simple-chat-app
   ```

2. Configure the application properties:
   Edit `application.properties` and add:
   ```properties
   socket-server.host=localhost
   socket-server.port=8085
   ```

3. Run the application:
   ```sh
   mvn spring-boot:run
   ```
   or
   ```sh
   java -jar target/demo-0.0.1-SNAPSHOT.jar
   ```

4. Connect to the chat using a WebSocket client (like **Postman** or a frontend app).

## Example WebSocket Connection
- **Connect to WebSocket:** `ws://localhost:8085/socket.io/?EIO=4&transport=websocket`
- **Join a room:** `room=gizli` (or any other room name)
- **Send a message:**
  ```json
  {
    "content": "Hello World!"
  }
  ```
- **Receive messages** in the same room.

## Improvements & Next Steps
- Adding a **frontend** (React, Vue, or Angular)
- Storing chat history in a **database**
- Implementing **authentication** for users

Feel free to fork and modify the project!

---

**Made for learning purposes.**

