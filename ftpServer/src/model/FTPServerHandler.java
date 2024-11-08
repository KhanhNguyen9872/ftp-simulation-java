package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import application.FTPServerView;
import javafx.scene.control.TextArea;

public class FTPServerHandler implements Runnable {
	private int port;
	private String path;
	private String ipAddressSQL;
	private int portSQL;
	private String databaseSQL;
	private String usernameSQL;
	private String passwordSQL;
	private FTPServerView view;
	private ServerSocket serverSocket = null;
	private List<FTPClientHandler> clientHandlerList = null;
	

	public FTPServerHandler(int port, String path, String ipAddressSQL, int portSQL, String databaseSQL,
			String usernameSQL, String passwordSQL, FTPServerView view, ServerSocket serverSocket) {
		this.port = port;
		this.path = path;
		this.ipAddressSQL = ipAddressSQL;
		this.portSQL = portSQL;
		this.databaseSQL = databaseSQL;
		this.usernameSQL = usernameSQL;
		this.passwordSQL = passwordSQL;
		this.view = view;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.clientHandlerList = new ArrayList<>();
		
		Socket clientSocket = null;
        FTPClientHandler clientHandler = null;
        
        System.out.println("FTPServer started on port: " + serverSocket.getLocalPort() + "\nHome path: " + this.path);
        
        try {
        	while (true) {
    			clientSocket = this.serverSocket.accept();
                
                this.view.writeLog("Connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                
                try {
    				clientHandler = new FTPClientHandler(clientSocket, path, new FTPServerDatabase(ipAddressSQL, portSQL, databaseSQL, usernameSQL, passwordSQL));
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
                
                this.clientHandlerList.add(clientHandler);
                
                Thread thread = new Thread(clientHandler);
                thread.start();
                
            }
		} catch (Exception e) {
			// TODO: handle exception
			for (FTPClientHandler handler : clientHandlerList) {
				handler.stop();
			}
		}
        
	}
	
}
