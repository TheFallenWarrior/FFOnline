/*
 * The MIT License
 *
 * Copyright 2026 TheFallenWarrior.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ffonline.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class ServerController {
    private static final int MAX_CLIENTS = 20;
    private static final Logger LOGGER = Logger.getLogger(ServerController.class.getName());
    
    private final int port;
    ExecutorService threadPool;
    
    public ServerController(int port, boolean isQuiet){
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        if(isQuiet) LOGGER.setLevel(Level.SEVERE);
    }
    
    public void listen(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            LOGGER.log(Level.INFO, "Starter FFOnline server in port {0}.", port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                LOGGER.log(Level.INFO, "Client connected: {0}.", clientSocket.getRemoteSocketAddress());
                
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch(IOException e){
            LOGGER.log(Level.SEVERE, "Server error: {0}.", e.getMessage());
        } finally{
            threadPool.shutdown();
        }
    }
    
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        clientSocket.getOutputStream(), true)
            ) {
                String message;
                while ((message = in.readLine()) != null) {
                    /*System.out.println(
                        "Received from " + clientSocket.getRemoteSocketAddress() + ": " + message
                    );*/

                    // Echo back to the same client
                    out.println(message);
                }
            } catch (IOException e) {
                System.err.println(
                    "Connection error with " + clientSocket.getRemoteSocketAddress()
                );
                LOGGER.log(Level.WARNING, "Connection error with {0}.", clientSocket.getRemoteSocketAddress());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ignored) {}
                LOGGER.log(Level.INFO, "Client disconnected: {0}.", clientSocket.getRemoteSocketAddress());
            }
        }
    }
}
