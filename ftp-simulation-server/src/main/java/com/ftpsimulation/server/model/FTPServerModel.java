package com.ftpsimulation.server.model;

import java.net.*;
import java.nio.file.Paths;

public class FTPServerModel {
    private int port;
    private String path;
    private String ipAddressSQL = "127.0.0.1";
    private int portSQL = 3306;
    private String databaseSQL = "ftpSimulation";
    private String usernameSQL = "root";
    private String passwordSQL = "12345678";

    private ServerSocket serverSocket;
    
    public FTPServerModel() {
        
    }

    private void prepareSettings() throws Exception {
        System.out.println("Reading settings....");
        FTPServerDatabase ftpServerDatabase = new FTPServerDatabase(ipAddressSQL, portSQL, databaseSQL, usernameSQL, passwordSQL);
        this.port = ftpServerDatabase.getPortServer();
        this.path = ftpServerDatabase.getPathServer();

        this.path = Paths.get(this.path).normalize().toRealPath().toString();
    }

    public void start() throws Exception {
        prepareSettings();

        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (Exception e) {
            return;
        }
        
        Socket clientSocket;
        FTPClientHandler clientHandler;
        
        System.out.println("FTPServer started on port: " + this.serverSocket.getLocalPort() + "\nHome path: " + this.path);
        
        while (true) {
            clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            
            clientHandler = new FTPClientHandler(clientSocket, path, new FTPServerDatabase(ipAddressSQL, portSQL, databaseSQL, usernameSQL, passwordSQL));
            new Thread(clientHandler).start();
        }
    }
}
