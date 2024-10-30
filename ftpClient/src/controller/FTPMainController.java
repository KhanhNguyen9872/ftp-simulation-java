package controller;

import javafx.fxml.FXML;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import view.FTPMainView;
import model.FTPMainModel;

public class FTPMainController implements Initializable {
	private Stage primaryStage;
	
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
	
	private ContextMenu rightClickLocal;
	private ContextMenu rightClickRemote;
	
	// menu bar
	@FXML
	private MenuItem menuFileOpenLocal;
	@FXML
	private MenuItem menuFileExit;
	@FXML
	private MenuItem menuHelpAbout;
	
	//
	@FXML
	private ImageView copyFromLocal2RemoteImg;
	@FXML
	private ImageView copyFromRemote2LocalImg;
	
	@FXML
	private ImageView moveFromLocal2RemoteImg;
	@FXML
	private ImageView moveFromRemote2LocalImg;
	
	@FXML
	private ImageView renameLocalImg;
	@FXML
	private ImageView renameRemoteImg;
	
	@FXML
	private ImageView reloadLocalImg;
	@FXML
	private ImageView reloadRemoteImg;
	
	@FXML
	private ImageView mkdirLocalImg;
	@FXML
	private ImageView mkdirRemoteImg;
	
	@FXML
	private ImageView deleteLocalImg;
	@FXML
	private ImageView deleteRemoteImg;
	
	@FXML
	private ImageView propertiesLocalImg;
	@FXML
	private ImageView propertiesRemoteImg;
	
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
	        	Tooltip tooltip = new Tooltip((isFile ? "File: " : "Folder: ") + fileName + " (Local)");
				tooltip.setShowDelay(new Duration(200));
				Tooltip.install(label, tooltip);
	        	fileLocalListView.getItems().add(label);
			}
        	
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	};
	
	private void chdir(String dir) {
		try {
			this.ftpMainModel.chdir(dir);
		} catch (Exception e) {
			e.printStackTrace();
			this.ftpMainView.showMessage("Error", e.getMessage());
			// TODO: handle exception
		}
	}
	
	private void updateVboxRemote() {
		this.choosenRemoteListView = "";
		if (remotePath.isEmpty()) {
			return;
		};
		
		try {
			this.listFileRemote = this.ftpMainModel.getListRemoteFile();
			
			fileRemoteListView.getItems().clear();
			final Label[] label = {new Label()};
			
			for(Map.Entry<String, Boolean> entry: this.listFileRemote.entrySet()) {
				String fileName = entry.getKey();
				Boolean isFile = entry.getValue();
				
				if (fileName.equals("..")) {
					label[0] = new Label(fileName);
				} else {
					label[0] = new Label((isFile ? "📄" : "📁") + "  " + fileName);
				}
				
				label[0].setMaxWidth(Double.MAX_VALUE);
				label[0].setOnMouseClicked(e -> {
					String buttonName = ((Label)e.getSource()).getText();
					String name;
					if (buttonName.equals("..")) {
						name = buttonName;
					} else {
						name = buttonName.substring(4, buttonName.length());
					}
					
					MouseButton btn = e.getButton();
					int clickCount = e.getClickCount();
					
					if (btn.equals(MouseButton.PRIMARY)) {
						switch(clickCount) {
							case 1:
								{
									if (!buttonName.equals("..")) {
										String typeButton = buttonName.substring(0, 2);
										if (typeButton.equals("📄")) {
											this.choosenRemoteListViewIsFile = true;
										} else if (typeButton.equals("📁")) {
											this.choosenRemoteListViewIsFile = false;
										} else {
											this.ftpMainView.showMessage("type file/folder Error");
										}
						        		this.choosenRemoteListView = name;
									} else {
										this.choosenRemoteListViewIsFile = false;
										this.choosenRemoteListView = "";
									}
								}
				        		break;
							case 2:
								{
									chdir(name);
									reloadRemoteList();
								}
								break;
						}
					}
					
					if (btn.equals(MouseButton.SECONDARY)) {
						switch(clickCount) {
							case 1:
								{
									if (!buttonName.equals("..")) {
										String typeButton = buttonName.substring(0, 2);
										if (typeButton.equals("📄")) {
											this.choosenRemoteListViewIsFile = true;
										} else if (typeButton.equals("📁")) {
											this.choosenRemoteListViewIsFile = false;
										} else {
											this.ftpMainView.showMessage("type file/folder Error");
										}
						        		this.choosenRemoteListView = name;
						        		
						        		this.rightClickRemote.show(label[0], e.getScreenX(), e.getScreenY());
									} else {
										this.choosenRemoteListViewIsFile = false;
										this.choosenRemoteListView = "";
									}
								}
								break;
						}
					}
            	});
				Tooltip tooltip = new Tooltip((isFile ? "File: " : "Folder: ") + fileName + " (Remote)");
				tooltip.setShowDelay(new Duration(200));
				Tooltip.install(label[0], tooltip);
				fileRemoteListView.getItems().add(label[0]);
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
		this.copyFromLocal2RemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				this.copyItemLocal();
			}
		});
		
		this.copyFromRemote2LocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				this.copyItemRemote();
			}
		});
		
		Tooltip tooltipCopyFromLocal2Remote = new Tooltip("Copy file/folder from (Local) to (Remote)");
		tooltipCopyFromLocal2Remote.setShowDelay(new Duration(200));
		Tooltip.install(this.copyFromLocal2RemoteImg, tooltipCopyFromLocal2Remote);
		
		Tooltip tooltipCopyFromRemote2Local = new Tooltip("Copy file/folder from (Remote) to (Local)");
		tooltipCopyFromRemote2Local.setShowDelay(new Duration(200));
		Tooltip.install(this.copyFromRemote2LocalImg, tooltipCopyFromRemote2Local);
		
		// move
		this.moveFromLocal2RemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				this.moveItemLocal();
			}
		});
		
		this.moveFromRemote2LocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				this.moveItemRemote();
			}
		});
		
		Tooltip tooltipMoveFromLocal2Remote = new Tooltip("Move file/folder from (Local) to (Remote)");
		tooltipMoveFromLocal2Remote.setShowDelay(new Duration(200));
		Tooltip.install(this.moveFromLocal2RemoteImg, tooltipMoveFromLocal2Remote);
		
		Tooltip tooltipMoveFromRemote2Local = new Tooltip("Move file/folder from (Remote) to (Local)");
		tooltipMoveFromRemote2Local.setShowDelay(new Duration(200));
		Tooltip.install(this.moveFromRemote2LocalImg, tooltipMoveFromRemote2Local);
		
		// rename
		this.renameLocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenLocalListView == null || choosenLocalListView.isEmpty()) {
					this.ftpMainView.showMessage("Error", "You must select one file/folder!");
					return;
				}
				
				this.renameLocal();
			}
		});
		this.renameRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessage("Error", "You must select one file/folder!");
					return;
				}
				
				this.renameRemote();
			}
		});
		
		Tooltip tooltipRenameLocal = new Tooltip("Rename file/folder (Local)");
		tooltipRenameLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.renameLocalImg, tooltipRenameLocal);
		
		Tooltip tooltipRenameRemote = new Tooltip("Rename file/folder (Remote)");
		tooltipRenameRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.renameRemoteImg, tooltipRenameRemote);
		
		// reload
		this.reloadLocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				reloadLocalList();
			}
		});
		this.reloadRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				reloadRemoteList();
			}
		});
		
		Tooltip tooltipReloadLocal = new Tooltip("Refresh (Local)");
		tooltipReloadLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.reloadLocalImg, tooltipReloadLocal);
		
		Tooltip tooltipReloadRemote = new Tooltip("Refresh (Remote)");
		tooltipReloadRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.reloadRemoteImg, tooltipReloadRemote);
		
		// mkdir
		this.mkdirLocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				this.mkdirLocal();
			}
			
		});
		
		this.mkdirRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				this.mkdirRemote();
			}
		});
		
		Tooltip tooltipMkdirLocal = new Tooltip("Create a folder (Local)");
		tooltipMkdirLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.mkdirLocalImg, tooltipMkdirLocal);
		
		Tooltip tooltipMkdirRemote = new Tooltip("Create a folder (Remote)");
		tooltipMkdirRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.mkdirRemoteImg, tooltipMkdirRemote);
		
		// delete
		this.deleteLocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenLocalListView == null || choosenLocalListView.isEmpty()) {
					this.ftpMainView.showMessage("Error", "You must select one file/folder!");
					return;
				}
				
				this.deleteLocal();
			}
		});
		
		this.deleteRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessage("Error", "You must select one file/folder!");
					return;
				}
				
				this.deleteRemote();
			}
		});
		
		Tooltip tooltipDeleteLocal = new Tooltip("Delete a file/folder (Local)");
		tooltipDeleteLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.deleteLocalImg, tooltipDeleteLocal);
		
		Tooltip tooltipDeleteRemote = new Tooltip("Delete a file/folder (Remote)");
		tooltipDeleteRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.deleteRemoteImg, tooltipDeleteRemote);
		
		// properties
		this.propertiesLocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenLocalListView == null || choosenLocalListView.isEmpty()) {
					this.ftpMainView.showMessage("Error", "You must select one file/folder!");
					return;
				}
				
				this.propertiesLocal();
			}
		});
		this.propertiesRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessage("Error", "You must select one file/folder!");
					return;
				}
				
				this.propertiesRemote();
			}
		});
		
		Tooltip tooltipPropertiesLocal = new Tooltip("Properties file/folder (Local)");
		tooltipPropertiesLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.deleteLocalImg, tooltipPropertiesLocal);
		
		Tooltip tooltipPropertiesRemote = new Tooltip("Properties file/folder (Remote)");
		tooltipPropertiesRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.deleteRemoteImg, tooltipPropertiesRemote);
	}
	
	private void moveItemRemote() {
		// TODO Auto-generated method stub
		
	}
	
	private void moveItemLocal() {
		
	}

	private void copyItemLocal() {
		// TODO Auto-generated method stub
		
	};
	
	private void copyItemRemote() {
		// TODO Auto-generated method stub
		
	}
	
	private void propertiesRemote() {
		// TODO Auto-generated method stub
		
	}

	private void propertiesLocal() {
		// TODO Auto-generated method stub
		
	}

	private void renameRemote() {
		String oldName = this.choosenRemoteListView;
		if (oldName == null || oldName.isEmpty()) {
			return;
		}
		
		if (oldName.equals("..")) {
			return;
		}
		String newName = this.ftpMainView.showTextInput("Rename [" + oldName + "] (remote)", "Input new name for [" + oldName + "]", "New name: ");
		
		if (newName == null || newName.isEmpty()) {
			return;
		}
		
		try {
			if (this.ftpMainModel.renameRemote(oldName, newName)) {
				reloadRemoteList();
			} else {
				this.ftpMainView.showMessage("Error", "Cannot rename to [" + newName + "]");
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			this.ftpMainView.showMessage("Error", e1.getMessage());
		}
	}

	private void renameLocal() {
		// TODO Auto-generated method stub
		
	}

	private void mkdirLocal() {
		String folderName = this.ftpMainView.showTextInput("Create folder (remote)", "Input folder name", "Name: ");
		
		if (folderName == null || folderName.isEmpty()) {
			return;
		}
		
		if (this.ftpMainModel.mkdirLocal(folderName)) {
			reloadLocalList();
		} else {
			this.ftpMainView.showMessage("Error", "Cannot create folder [" + folderName + "]");
		}
	}
	
	private void mkdirRemote() {
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
	}
	
	private void deleteLocal() {
		String name = (this.choosenLocalListViewIsFile) ? "file" : "folder";
		
		if (this.ftpMainView.askYesOrNo("Delete " + name, "Delete " + name + "? [" + choosenLocalListView + "]", "Local path: \"" + this.localPath + "\"")) {
			this.ftpMainModel.deleteLocal(choosenLocalListView);
			reloadLocalList();
		}
	}
	
	private void deleteRemote() {
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
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.ftpMainView = new FTPMainView();
		this.ftpMainModel = new FTPMainModel("C:\\");
		
		this.fileLocalListView = new ListView<>();
		this.fileRemoteListView = new ListView<>();
		
		this.vboxLocal.getChildren().add(fileLocalListView);
		this.vboxRemote.getChildren().add(fileRemoteListView);
		
		prepareButton();
		prepareContextMenu();
		prepareMenuItem();
	}
	
	private void prepareMenuItem() {
		// file
		this.menuFileOpenLocal.setOnAction(e -> {
			
		});
		
		this.menuFileExit.setOnAction(e -> {
			primaryStage.close();
		});
		
		// help
		this.menuHelpAbout.setOnAction(e -> {
			
		});
	}

	private void prepareContextMenu() {
		// local
		
		
		// remote
		// Create a context menu with some menu items
        this.rightClickRemote = new ContextMenu();
        MenuItem copyItemRemote = new MenuItem("Copy");
        MenuItem moveItemRemote = new MenuItem("Move");
        MenuItem renameItemRemote = new MenuItem("Rename");
        MenuItem deleteItemRemote = new MenuItem("Delete");
        MenuItem propertiesItemRemote = new MenuItem("Properties");

        // Add items to the context menu
        this.rightClickRemote.getItems().addAll(copyItemRemote, moveItemRemote, renameItemRemote, deleteItemRemote, propertiesItemRemote);

        // Set actions for menu items (optional)
        copyItemRemote.setOnAction(e -> {
        	copyItemRemote();
        });
        moveItemRemote.setOnAction(e -> {
        	moveItemRemote();
        });
        renameItemRemote.setOnAction(e -> {
        	renameRemote();
        });
        deleteItemRemote.setOnAction(e -> {
        	deleteRemote();
        });
        propertiesItemRemote.setOnAction(e -> {
        	propertiesRemote();
        });
	}

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
};
