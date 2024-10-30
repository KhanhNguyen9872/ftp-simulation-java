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
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import view.FTPMainView;
import model.FTPMainModel;

public class FTPMainController implements Initializable {
	private String localPath;
	private String remotePath;
	
	private FTPMainView ftpMainView;
	private FTPMainModel ftpMainModel;
	
	private Map<String, Boolean> listFileLocal;
	private Map<String, Boolean> listFileRemote;
	
	private ListView<Label> fileLocalListView;
	private ListView<Label> fileRemoteListView;
	
	private String choosenRemoteListView;
	private boolean choosenRemoteListViewIsFile;
	
	private String choosenLocalListView;
	private boolean choosenLocalListViewIsFile;
	
	@FXML
	private ImageView copyFromLocal2Remote;
	@FXML
	private ImageView copyFromRemote2Local;
	
	@FXML
	private ImageView moveFromLocal2Remote;
	@FXML
	private ImageView moveFromRemote2Local;
	
	@FXML
	private ImageView renameLocal;
	@FXML
	private ImageView renameRemote;
	
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
		this.localPath = this.ftpMainModel.getCurrentLocalPath();
	};
	
	private void updateVboxLocal() {
		this.choosenLocalListView = "";
		if (localPath.isEmpty()) {
			return;
		};
		
		try {
			this.listFileLocal = this.ftpMainModel.getListLocalFile();
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
					String typeButton = buttonName.substring(0, 2);
					if (typeButton.equals("📄")) {
						this.choosenRemoteListViewIsFile = true;
					} else if (typeButton.equals("📁")) {
						this.choosenRemoteListViewIsFile = false;
					} else {
						this.ftpMainView.showMessage("type file/folder Error");
					}
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
			this.remotePath = this.ftpMainModel.getCurrentRemotePath();
		} catch (Exception e) {
			this.ftpMainView.showMessage(e.getMessage());
		};
	};
	
	private void prepareButton() {
		// copy
		this.copyFromLocal2Remote.setOnMouseClicked(e -> {
			this.ftpMainView.showMessage("Local2Remote");
		});
		
		this.copyFromRemote2Local.setOnMouseClicked(e -> {
			this.ftpMainView.showMessage("Remote2Local");
		});
		
		Tooltip tooltipCopyFromLocal2Remote = new Tooltip("Copy file/folder from (Local) to (Remote)");
		tooltipCopyFromLocal2Remote.setShowDelay(new Duration(200));
		Tooltip.install(this.copyFromLocal2Remote, tooltipCopyFromLocal2Remote);
		
		Tooltip tooltipCopyFromRemote2Local = new Tooltip("Copy file/folder from (Remote) to (Local)");
		tooltipCopyFromRemote2Local.setShowDelay(new Duration(200));
		Tooltip.install(this.copyFromRemote2Local, tooltipCopyFromRemote2Local);
		
		// move
		this.moveFromLocal2Remote.setOnMouseClicked(e -> {
			this.ftpMainView.showMessage("Local2Remote");
		});
		
		this.moveFromRemote2Local.setOnMouseClicked(e -> {
			this.ftpMainView.showMessage("Remote2Local");
		});
		
		Tooltip tooltipMoveFromLocal2Remote = new Tooltip("Move file/folder from (Local) to (Remote)");
		tooltipMoveFromLocal2Remote.setShowDelay(new Duration(200));
		Tooltip.install(this.moveFromLocal2Remote, tooltipMoveFromLocal2Remote);
		
		Tooltip tooltipMoveFromRemote2Local = new Tooltip("Move file/folder from (Remote) to (Local)");
		tooltipMoveFromRemote2Local.setShowDelay(new Duration(200));
		Tooltip.install(this.moveFromRemote2Local, tooltipMoveFromRemote2Local);
		
		// rename
		this.renameLocal.setOnMouseClicked(e -> {
			
		});
		this.renameRemote.setOnMouseClicked(e -> {
			
		});
		
		Tooltip tooltipRenameLocal = new Tooltip("Rename file/folder (Local)");
		tooltipRenameLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.renameLocal, tooltipRenameLocal);
		
		Tooltip tooltipRenameRemote = new Tooltip("Rename file/folder (Remote)");
		tooltipRenameRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.renameRemote, tooltipRenameRemote);
		
		// reload
		this.reloadLocal.setOnMouseClicked(e -> {
			reloadLocalList();
		});
		this.reloadRemote.setOnMouseClicked(e -> {
			reloadRemoteList();
		});
		
		Tooltip tooltipReloadLocal = new Tooltip("Refresh (Local)");
		tooltipReloadLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.reloadLocal, tooltipReloadLocal);
		
		Tooltip tooltipReloadRemote = new Tooltip("Refresh (Remote)");
		tooltipReloadRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.reloadRemote, tooltipReloadRemote);
		
		// mkdir
		this.mkdirLocal.setOnMouseClicked(e -> {
			String folderName = this.ftpMainView.showTextInput("Create folder (remote)", "Input folder name", "Name: ");
			
			if (folderName == null || folderName.isEmpty()) {
				return;
			}
			
			if (this.ftpMainModel.mkdirLocal(folderName)) {
				reloadLocalList();
			} else {
				this.ftpMainView.showMessage("Error", "Cannot create folder [" + folderName + "]");
			}
		});
		
		this.mkdirRemote.setOnMouseClicked(e -> {
			String folderName = this.ftpMainView.showTextInput("Create folder (remote)", "Input folder name", "Name: ");
			
			if (folderName == null || folderName.isEmpty()) {
				return;
			}
			
			try {
				if (this.ftpMainModel.mkdirRemote(folderName)) {
					reloadRemoteList();
				} else {
					this.ftpMainView.showMessage("Error", "Cannot create folder [" + folderName + "]");
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				this.ftpMainView.showMessage("Error", e1.getMessage());
			}
		});
		
		Tooltip tooltipMkdirLocal = new Tooltip("Create a folder (Local)");
		tooltipMkdirLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.mkdirLocal, tooltipMkdirLocal);
		
		Tooltip tooltipMkdirRemote = new Tooltip("Create a folder (Remote)");
		tooltipMkdirRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.mkdirRemote, tooltipMkdirRemote);
		
		// delete
		this.deleteLocal.setOnMouseClicked(e -> {
			if (choosenLocalListView == null || choosenLocalListView.isEmpty()) {
				this.ftpMainView.showMessage("Error", "You must select one file/folder!");
				return;
			}
			
			String name = (this.choosenLocalListViewIsFile) ? "file" : "folder";
			
			if (this.ftpMainView.askYesOrNo("Delete " + name, "Delete " + name + "? [" + choosenLocalListView + "]", "Local path: \"" + this.localPath + "\"")) {
				this.ftpMainModel.deleteLocal(choosenLocalListView);
				reloadLocalList();
			}
		});
		
		this.deleteRemote.setOnMouseClicked(e -> {
			if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
				this.ftpMainView.showMessage("Error", "You must select one file/folder!");
				return;
			}
			
			String name = (this.choosenRemoteListViewIsFile) ? "file" : "folder";
			
			if (this.ftpMainView.askYesOrNo("Delete " + name, "Delete " + name + "? [" + choosenRemoteListView + "]", "Remote path: \"" + this.remotePath + "\"")) {
				try {
					if (this.choosenLocalListViewIsFile) {
						this.ftpMainModel.deleteFileRemote(choosenRemoteListView);
					} else {
						this.ftpMainModel.deleteFolderRemote(choosenRemoteListView);
					}
					
					reloadRemoteList();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					this.ftpMainView.showMessage("Error", e1.getMessage());
				}
				
			}
		});
		
		Tooltip tooltipDeleteLocal = new Tooltip("Delete a file/folder (Local)");
		tooltipDeleteLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.deleteLocal, tooltipDeleteLocal);
		
		Tooltip tooltipDeleteRemote = new Tooltip("Delete a file/folder (Remote)");
		tooltipDeleteRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.deleteRemote, tooltipDeleteRemote);
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
