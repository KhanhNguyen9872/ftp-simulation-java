package controller;

import javafx.fxml.FXML;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import view.FTPMainView;
import model.FTPMainModel;

public class FTPMainController implements Initializable {
	private String localPath = null;
	private String remotePath = null;
	
	private FTPMainView ftpMainView;
	private FTPMainModel ftpMainModel;
	
	private Map<String, Boolean> listFileLocal;
	private Map<String, Boolean> listFileRemote;
	
	private ListView<Label> fileLocalListView;
	private ListView<Label> fileRemoteListView;
	
	private String choosenRemoteListView;
	private String choosenLocalListView;
	
	@FXML
	private Button sendFromLocal2Remote;
	@FXML
	private Button sendFromRemote2Local;
	
	@FXML
	private ImageView reloadLocal;
	@FXML
	private ImageView reloadRemote;
	
	@FXML
	private ImageView mkdirLocal;
	@FXML
	private ImageView mkdirRemote;
	
	@FXML
	private ImageView deleteLocal;
	@FXML
	private ImageView deleteRemote;
	
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
	
	private void reloadLocalList() {
		getCurrentLocalPath();
		updateVboxLocal();
	}
	
	private void reloadRemoteList() {
		try {
			getCurrentRemotePath();
			updateVboxRemote();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		reloadLocalList();
		reloadRemoteList();
	};
	
	private void getCurrentLocalPath() {
		if (localPath == null) {
			this.localPath = "C:\\";
		};
	};
	
	private void updateVboxLocal() {
		this.choosenLocalListView = "";
		if (localPath.isEmpty()) {
			return;
		};
		
		try {
			this.listFileLocal = this.ftpMainModel.getListLocalFile(localPath);
			fileLocalListView.getItems().clear();
			
			for(Map.Entry<String, Boolean> entry: this.listFileLocal.entrySet()) {
				String fileName = entry.getKey();
				Boolean isFile = entry.getValue();
				
				Label label = new Label((isFile ? "📄" : "📁") + "  " + fileName);
	        	label.setMaxWidth(Double.MAX_VALUE);
	        	label.setOnMouseClicked(e -> {
	        		String buttonName = ((Label)e.getSource()).getText();
	        		this.choosenLocalListView = buttonName.substring(4, buttonName.length());
	        	});
	        	fileLocalListView.getItems().add(label);
			}
        	
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	};
	
	private void updateVboxRemote() {
		this.choosenRemoteListView = "";
		if (remotePath.isEmpty()) {
			return;
		};
		
		try {
			this.listFileRemote = this.ftpMainModel.getListRemoteFile();
			
			fileRemoteListView.getItems().clear();
			Label label = null;
			
			for(Map.Entry<String, Boolean> entry: this.listFileRemote.entrySet()) {
				String fileName = entry.getKey();
				Boolean isFile = entry.getValue();
				
				label = new Label((isFile ? "📄" : "📁") + "  " + fileName);
				label.setMaxWidth(Double.MAX_VALUE);
				label.setOnMouseClicked(e -> {
					String buttonName = ((Label)e.getSource()).getText();
	        		this.choosenRemoteListView = buttonName.substring(4, buttonName.length());
            	});
				fileRemoteListView.getItems().add(label);
			};
		} catch (Exception e) {
			this.ftpMainView.showMessage("File not found", e.getMessage());
			e.printStackTrace();
		};
	};
	
	private void getCurrentRemotePath() {
		try {
			this.remotePath = this.ftpMainModel.getCurrentPath();
		} catch (Exception e) {
			this.ftpMainView.showMessage(e.getMessage());
		};
	};
	
	private void prepareButton() {
		this.sendFromLocal2Remote.setOnMouseClicked(e -> {
			this.ftpMainView.showMessage("Local2Remote");
		});
		
		this.sendFromRemote2Local.setOnMouseClicked(e -> {
			this.ftpMainView.showMessage("Remote2Local");
		});
		
		this.reloadLocal.setOnMouseClicked(e -> {
			reloadLocalList();
		});
		
		this.reloadRemote.setOnMouseClicked(e -> {
			reloadRemoteList();
		});
		
		this.mkdirLocal.setOnMouseClicked(e -> {
			String folderName = this.ftpMainView.showTextInput("Tạo thư mục tại local", "Nhập tên thư mục", "Tên: ");
			
			this.ftpMainModel.mkdirLocal(folderName);
		});
		
		this.mkdirRemote.setOnMouseClicked(e -> {
			
		});
		
		this.deleteLocal.setOnMouseClicked(e -> {
			if (choosenLocalListView == null || choosenLocalListView.isEmpty()) {
				this.ftpMainView.showMessage("Error", "You must select one file/folder!");
				return;
			}
			
			if (this.ftpMainView.askYesOrNo("Xóa file", "Bạn có muốn xóa file [" + choosenLocalListView + "]?", "Local path: \"" + this.localPath + "\"")) {
				
				reloadLocalList();
			}
		});
		
		this.deleteRemote.setOnMouseClicked(e -> {
			if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
				this.ftpMainView.showMessage("Error", "You must select one file/folder!");
				return;
			}
			
			if (this.ftpMainView.askYesOrNo("Xóa file", "Bạn có muốn xóa file [" + choosenRemoteListView + "]?", "Remote path: \"" + this.remotePath + "\"")) {
				
				reloadRemoteList();
			}
		});
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.ftpMainView = new FTPMainView();
		this.ftpMainModel = new FTPMainModel();
		
		this.fileLocalListView = new ListView<>();
		this.fileRemoteListView = new ListView<>();
		
		this.vboxLocal.getChildren().add(fileLocalListView);
		this.vboxRemote.getChildren().add(fileRemoteListView);
		
		prepareButton();
	};
};
