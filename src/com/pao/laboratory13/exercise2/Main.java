package com.pao.laboratory13.exercise2;

import com.pao.laboratory13.exercise1.ProtocolEngine;
import com.pao.laboratory13.exercise1.State;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int PORT = 9000;
    private static final AtomicInteger activeClients = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        // Start Server in a separate thread
        Thread serverThread = new Thread(() -> startServer());
        serverThread.start();

        // Give server a moment to start
        Thread.sleep(1000);

        // Start Clients
        Thread client1 = new Thread(() -> runClient("alice", new String[]{"AUTH alice", "OPEN", "SEND hello from alice", "HISTORY", "CLOSE"}));
        Thread client2 = new Thread(() -> runClient("bob", new String[]{"AUTH bob", "OPEN", "BROADCAST ping from bob", "HISTORY", "CLOSE"}));

        client1.start();
        client2.start();

        client1.join();
        client2.join();

        // Wait for server to finish processing
        System.out.println("[MAIN] Clients finished. Waiting for server to shutdown...");
        
        // In a real scenario, we might want to signal the server to stop.
        // Here we'll just wait a bit and let it be.
        Thread.sleep(2000);
        System.exit(0);
    }

    private static void startServer() {
        System.out.println("[SERVER] Listening on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ExecutorService pool = Executors.newCachedThreadPool();
            
            // For the sake of the demo, we'll accept 2 clients and then stop accepting.
            while (true) {
                Socket clientSocket = serverSocket.accept();
                activeClients.incrementAndGet();
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Error: " + e.getMessage());
        }
    }

    private static void runClient(String name, String[] commands) {
        System.out.println("[CLIENT-" + name + "] Connecting...");
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("[CLIENT-" + name + "] Connected");
            for (String cmd : commands) {
                System.out.println("[CLIENT-" + name + "] >> " + cmd);
                out.println(cmd);
                String response = in.readLine();
                System.out.println("[CLIENT-" + name + "] << " + response);
                Thread.sleep(500); // simulate some delay
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("[CLIENT-" + name + "] Error: " + e.getMessage());
        } finally {
            System.out.println("[CLIENT-" + name + "] Disconnected");
            activeClients.decrementAndGet();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ProtocolEngine engine;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.engine = new ProtocolEngine();
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String line;
                while ((line = in.readLine()) != null) {
                    String response = engine.processCommand(line);
                    out.println(response);
                    if (engine.getState() == State.CLOSED) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("[SERVER-HANDLER] Error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
