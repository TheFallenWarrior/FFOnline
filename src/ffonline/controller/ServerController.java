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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thefa
 */
public class ServerController {
    private static final Logger LOGGER = Logger.getLogger(ServerController.class.getName());
    
    private final int port;
    private final ExecutorService threadPool;
    
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    
    public ServerController(int port, int maxClients, boolean isQuiet){
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(maxClients);
        if(isQuiet) LOGGER.setLevel(Level.SEVERE);
    }
    
    public void listen(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            LOGGER.log(Level.INFO, "Started FFOnline server in port {0}.", port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                LOGGER.log(Level.INFO, "Client connected: {0}.", clientSocket.getRemoteSocketAddress());
                
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                threadPool.execute(client);
            }
        } catch(IOException e){
            LOGGER.log(Level.SEVERE, "Server error: {0}.", e.getMessage());
        } finally{
            threadPool.shutdown();
        }
    }
    
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private String username;
        private GameStateManager game;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run(){
            try(
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
                );
            ){
                out = new PrintWriter(
                    clientSocket.getOutputStream(),
                        true
                );
                
                out.println("Enter your username:");
                username = in.readLine();
                
                if(username == null || username.isBlank()) username = "Guest";
                
                out.println("Welcome, "+username+"!");
                
                game = new GameStateManager(in, out);
                
                String command;
                while((command = in.readLine()) != null){
                    runGameCommand(command);
                }
            } catch(IOException e){
                LOGGER.log(Level.WARNING, "Connection error with {0}.", clientSocket.getRemoteSocketAddress());
            } finally{
                cleanup();
            }
        }
        
        private void runGameCommand(String command){
            String[] splitCommand = command.split(" ", 2);
            switch (splitCommand[0]) {
                case "ooc" -> broadcast(username+" says \""+splitCommand[1]+"\"");
                
                case "who" -> {
                    out.println("Logged in users:");
                    for(ClientHandler client : clients){
                        if(client == this) out.println(" "+client.username+" (you)");
                        else out.println(" "+client.username);
                    }
                }
                
                case "logout" -> cleanup();
                
                case "" -> {}
                
                default -> out.println("Unknown command: \""+splitCommand[0]+"\"");
            }
        }
        
        private void broadcast(String message){
            for(ClientHandler client : clients){
                if(client != this) client.out.println(message);
            }
        }
        
        private void cleanup(){
            clients.remove(this);
            try{
                clientSocket.close();
            } catch(IOException ignored){}
            LOGGER.log(Level.INFO, "Client disconnected: {0}.", clientSocket.getRemoteSocketAddress());
        }
    }
}
