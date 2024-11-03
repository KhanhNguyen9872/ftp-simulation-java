package com.ftpsimulation.server.model;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FTPServerModel {
    private int port = 21;
    private String path;
    private String ipAddressSQL = "127.0.0.1";
    private int portSQL = 3306;
    private String databaseSQL = "ftpSimulation";
    private String usernameSQL = "root";
    private String passwordSQL = "12345678";

    private ServerSocket serverSocket;
    
    public FTPServerModel() {
        
    }

    private void prepareSettings() {
        System.out.println("Reading settings....");
        FTPServerDatabase ftpServerDatabase = new FTPServerDatabase(ipAddressSQL, portSQL, databaseSQL, usernameSQL, passwordSQL);
        this.port = ftpServerDatabase.getPortServer();
        this.path = ftpServerDatabase.getPathServer();

        try {
            this.path = Paths.get(this.path).normalize().toRealPath().toString();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    public void start() throws IOException {
        prepareSettings();

        this.serverSocket = new ServerSocket(this.port);
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

    public void setPort(int port) {
        this.port = port;
    }

    public void setPath(String path) {
        try {
            this.path = Paths.get(path).normalize().toRealPath().toString();
        } catch (Exception e) {
            System.err.println("NOT FOUND: " + this.path);
        }
        
    }
}
