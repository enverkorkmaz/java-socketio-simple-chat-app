package com.enver.demo.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.enver.demo.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SocketModule {

    private static final Logger log = LoggerFactory.getLogger(SocketModule.class);
    private final SocketIOServer socketIOServer;
    private static final String DEFAULT_ROOM = "general";
    private final ConcurrentMap<String, Boolean> connectedClients = new ConcurrentHashMap<>();

    public SocketModule(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());
        socketIOServer.addEventListener("send_message", Message.class, onMessageReceived());
    }

    private DataListener<Message> onMessageReceived() {
        return (senderClient, data, ackSender) -> {
            String room = senderClient.getHandshakeData().getSingleUrlParam("room");
            if (room == null || room.isEmpty()) {
                room = DEFAULT_ROOM;
            }

            log.info(String.format("SocketID: %s -> %s", senderClient.getSessionId(), data.getContent()));

            var roomOperations = senderClient.getNamespace().getRoomOperations(room);
            if (roomOperations != null) {
                roomOperations.getClients().forEach(client -> {
                    if (!client.getSessionId().equals(senderClient.getSessionId())) {
                        client.sendEvent("get_message", data.getContent());
                    }
                });
            } else {
                log.warn("Message received but room '{}' does not exist", room);
            }
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            String room = client.getHandshakeData().getSingleUrlParam("room");
            if (room == null || room.isEmpty()) {
                room = DEFAULT_ROOM;
            }

            String sessionId = client.getSessionId().toString();
            if (connectedClients.putIfAbsent(sessionId, true) == null) {
                client.joinRoom(room);
                log.info(String.format("SocketID: %s connected to %s", sessionId, room));

                var roomOperations = client.getNamespace().getRoomOperations(room);
                if (roomOperations != null) {
                    String finalRoom = room;
                    roomOperations.getClients().forEach(existingClient -> {
                        if (!existingClient.getSessionId().equals(client.getSessionId())) {
                            existingClient.sendEvent("get_message", sessionId + " connected to " + finalRoom);
                        }
                    });
                }
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            log.info(String.format("SocketID: %s disconnected", sessionId));

            connectedClients.remove(sessionId);

            String room = client.getHandshakeData().getSingleUrlParam("room");
            if (room == null || room.isEmpty()) {
                room = DEFAULT_ROOM;
            }

            var roomOperations = client.getNamespace().getRoomOperations(room);
            if (roomOperations != null) {
                roomOperations.getClients().forEach(existingClient -> {
                    if (!existingClient.getSessionId().equals(client.getSessionId())) {
                        existingClient.sendEvent("get_message", sessionId + " disconnected");
                    }
                });
            }
        };
    }
}
