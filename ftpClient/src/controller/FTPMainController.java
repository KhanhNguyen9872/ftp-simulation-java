package controller;

import javafx.event.EventTarget;
import javafx.fxml.FXML;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import view.FTPMainView;
import model.FTPMainModel;

public class FTPMainController implements Initializable {
	private String localPath = null;
	private String remotePath = null;
	private FTPMainView ftpMainView;
	private FTPMainModel ftpMainModel;
	private ListView<String> fileLocalListView;
	private ListView<String> fileRemoteListView;
	
	@FXML
	private VBox vboxLocal;
	@FXML
	private VBox vboxLocalToolBox;
	@FXML
	private VBox vboxRemote;
	@FXML
	private VBox vboxRemoteToolBox;
	
	
	public void setSocket(Socket sock) {
		this.ftpMainModel.setSocket(sock);
	};
	
	public void run() {
		getCurrentLocalPath();
		updateVboxLocal();
		getCurrentRemotePath();
		updateVboxRemote();
	};
	
	private void getCurrentLocalPath() {
		if (localPath == null) {
			this.localPath = "C:\\";
		};
	};
	
	private void updateVboxLocal() {
		if (localPath.isEmpty()) {
			return;
		};
		
		fileLocalListView.getItems().clear();
		
		try (Stream<Path> paths = Files.list(Paths.get(localPath))) {
            paths.forEach(path -> {
            	fileLocalListView.getItems().add(path.getFileName().toString());
            });
        } catch (IOException e) {
        	e.printStackTrace();
        };
	};
	
	private void updateVboxRemote() {
		if (remotePath.isEmpty()) {
			return;
		};
		
		try {
			List<String> listFile = this.ftpMainModel.getListFile();
			
			fileRemoteListView.getItems().clear();
			
			for(String s: listFile) {
				fileRemoteListView.getItems().add(s);
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	};
	
	private void getCurrentRemotePath() {
		try {
			this.remotePath = this.ftpMainModel.getCurrentPath();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.ftpMainView.showMessage(e.getMessage());
		};
	};
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.ftpMainView = new FTPMainView();
		this.ftpMainModel = new FTPMainModel();
		
		this.fileLocalListView = new ListView<>();
		this.fileRemoteListView = new ListView<>();
		
		this.vboxLocal.getChildren().add(fileLocalListView);
		this.vboxRemote.getChildren().add(fileRemoteListView);
	};
};
