package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;

import application.FTPServerView;

public class FTPServerModel {
    private int port;
    private String path;
    private String ipAddressSQL = "127.0.0.1";
    private int portSQL = 3306;
    private String databaseSQL = "ftpSimulation";
    private String usernameSQL = "root";
    private String passwordSQL = "12345678";
    private FTPServerDatabase ftpServerDatabase;
    private Thread serverHandler;
    private FTPServerView view;
    private ServerSocket serverSocket;
    
    public FTPServerModel() {
        
    }
    
    public void setView(FTPServerView view) {
    	this.view = view;
    }

    public void prepareSettings() throws Exception {
        System.out.println("Reading settings....");
        this.ftpServerDatabase = new FTPServerDatabase(ipAddressSQL, portSQL, databaseSQL, usernameSQL, passwordSQL);
        this.port = ftpServerDatabase.getPortServer();
        this.path = ftpServerDatabase.getPathServer();

        this.path = Paths.get(this.path).normalize().toRealPath().toString();
    }

    public boolean start() {
    	try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
        
    	FTPServerHandler ftpServerHandler = new FTPServerHandler(port, path, ipAddressSQL, portSQL, databaseSQL, usernameSQL, passwordSQL, this.view, this.serverSocket);
    	
    	this.serverHandler = new Thread(ftpServerHandler);
    	this.serverHandler.start();
    	return true;
    }

	public void stop() {
		// TODO Auto-generated method stub
		this.serverHandler.interrupt();
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void saveData() {
		// TODO Auto-generated method stub
		if (this.ftpServerDatabase != null) {
			this.ftpServerDatabase.saveSettings(path, port);
		}
	}
	
	
	
}
