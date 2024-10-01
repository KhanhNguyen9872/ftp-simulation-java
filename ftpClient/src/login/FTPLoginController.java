package login;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import lib.*;
import main.FTPMainController;

public class FTPLoginController implements Initializable {
	private FTPLoginView ftpLoginView;
	
	@FXML
	private TextField tf_IPAddress;
	@FXML
	private TextField tf_Port;
	@FXML
	private TextField tf_Username;
	@FXML
	private TextField tf_Password;
	
	public void connectButton(ActionEvent e) throws Exception {
		String ipAddress = this.tf_IPAddress.getText();
		String port = this.tf_Port.getText();
		String username = this.tf_Username.getText();
		String password = this.tf_Password.getText();
		
		if (ipAddress.isEmpty() || port.isEmpty() || username.isEmpty() || password.isEmpty()) {
			System.out.println("empty");
			return;
		}
		
		try {
			Integer.parseInt(port);
		} catch (Exception ex) {
			System.out.println(ex);
			return;
		}
		
		FTPConfig config = new FTPConfig(ipAddress, port, username, password);
		this.startFTPMain(config);
	}
	
	private void startFTPMain(FTPConfig config) throws Exception {
		this.ftpLoginView.close();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("File Explorer | FTP Simulation");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		FTPMainController ftpMainController = loader.getController();
		ftpMainController.setFtpConfig(config);
	}
	
	public void exitButton(ActionEvent e) {
		this.ftpLoginView.close();
	}
	
	public void setFtpLoginView(FTPLoginView ftpLoginView) {
		this.ftpLoginView = ftpLoginView;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
