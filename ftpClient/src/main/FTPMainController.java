package main;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lib.FTPConfig;
import login.*;

public class FTPMainController implements Initializable {
	private FTPConfig ftpConfig;
	
	public void run() throws Exception {
		
	}
	
	public void setFtpConfig(FTPConfig ftpConfig) {
		this.ftpConfig = ftpConfig;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}
}
