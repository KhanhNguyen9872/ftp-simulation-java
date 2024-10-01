package login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.*;
import lib.FTPConfig;
import javafx.scene.*;

public class FTPLoginView {
	private boolean closed = false;
	private FTPConfig ftpConfig;
	private Stage primaryStage;
	
	public FTPLoginView(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		this.primaryStage.setTitle("Login | FTP Simulation");
		this.primaryStage.setScene(scene);
		this.primaryStage.setResizable(false);
		
		FTPLoginController ftpLoginController = loader.getController();
		ftpLoginController.setFtpLoginView(this);
	}
	
	public void show() {
		this.primaryStage.show();
	}
	
	public void close() {
		this.primaryStage.close();
	}
}
