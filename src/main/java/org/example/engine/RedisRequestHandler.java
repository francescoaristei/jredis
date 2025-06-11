package org.example.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.RedisDatabase;
import org.example.resp.Deserializer;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.errors.SimpleError;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class RedisRequestHandler implements Runnable{
    private final Socket clientSocket;
    private static final Logger log = LogManager.getLogger(RedisRequestHandler.class);
    private final RedisRequestProcessor redisRequestProcessor;

    public RedisRequestHandler(Socket clientSocket, RedisDatabase redisDatabase) {
        this.clientSocket = clientSocket;
        this.redisRequestProcessor = new RedisRequestProcessor(redisDatabase);
    }

    @Override
    public void run() {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())))
        {
            Deserializer deserializer = new Deserializer();
            int arrayCommand;
            // possible better strategy: to accumulate input stream into char[]
            while((arrayCommand = bufferedReader.read()) >= 0) {
                if ((char)arrayCommand != '*') {
                    bufferedWriter.write(new SimpleError("ERR: commands are Resp Arrays").serialize().toString());
                }
                RespDataType request = deserializer.deserializeRequest(bufferedReader);
                String result = redisRequestProcessor.processRequest((RespArray) request).serialize().toString();
                bufferedWriter.write(result);
                bufferedWriter.flush();
            }
        } catch (SocketException e) {
            try {
                clientSocket.close();
                log.error("Socket exception: {}", e.getMessage());
            } catch (IOException ex) {
                log.error("IO error while closing client socket: {}", e.getMessage());
            }
        }  catch (IOException e) {
            log.error("IO error in Input/Output streams: {}", e.getMessage());
        }
    }
}
