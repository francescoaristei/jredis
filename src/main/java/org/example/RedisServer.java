package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.RedisDatabase;
import org.example.engine.RedisRequestHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisServer {
    private static final Logger log = LogManager.getLogger(RedisServer.class);
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final RedisDatabase redisDatabase = RedisDatabase.getInstance();

    public static void main(String[] args) {
        log.info("starting redis server...");
        startRedisServer();
    }

    private static void startRedisServer() {
        try(ServerSocket serverSocket = new ServerSocket(6379)) {
            log.info("server started.");
            while(true) {
                executor.execute(new RedisRequestHandler(serverSocket.accept(), redisDatabase));
            }
        } catch(IOException e) {
            log.error("IOException while creating Server Socket, server shutdown: {}", e.getMessage());
            executor.shutdown();
        }
    }
}