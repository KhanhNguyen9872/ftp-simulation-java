package controller;

import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lib.*;
import view.FTPLoginView;
import model.FTPLoginModel;
import run.FTPMainRun;

public class FTPLoginController implements Initializable {
	private FTPLoginView ftpLoginView;
	private FTPLoginModel ftpLoginModel;
	private Stage stage;
	
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
			this.ftpLoginView.showMessage("Do not leave any information blank!");
			return;
		};
		
		// check valid port
		{
			try {
				int p = Integer.parseInt(port);
				if ((p < 1) || (p > 65535)) {
					throw new Exception("Port must in [1 - 65535]");
				};
			} catch (Exception ex) {
				this.ftpLoginView.showMessage(ex.getMessage());
				return;
			};
		};
		
		Socket sock;
		FTPConfig config = new FTPConfig(ipAddress, port, username, password);
		
		try {
			sock = this.ftpLoginModel.connect(config);
		} catch (Exception ex) {
			this.ftpLoginView.showMessage(ex.getMessage());
			return;
		};
		
		if (sock != null) {
			this.startFTPMain(sock);
		} else {
			this.ftpLoginView.showMessage("Cannot connect to " + ipAddress + ":" + port);
		};
		
	};

	private void startFTPMain(Socket sock) throws Exception {
		this.stage.hide();
		new FTPMainRun(sock);
	}
	
	public void exitButton(ActionEvent e) {
		this.stage.close();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		this.ftpLoginView = new FTPLoginView();
		this.ftpLoginModel = new FTPLoginModel();
	}
	
}
