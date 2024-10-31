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
			final Label[] label = {new Label()};
			
			for(Map.Entry<String, Boolean> entry: this.listFileLocal.entrySet()) {
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
											this.choosenLocalListViewIsFile = true;
										} else if (typeButton.equals("📁")) {
											this.choosenLocalListViewIsFile = false;
										} else {
											this.ftpMainView.showMessage("type file/folder Error");
										}
						        		this.choosenLocalListView = name;
									} else {
										this.choosenLocalListViewIsFile = false;
										this.choosenLocalListView = "";
									}
								}
				        		break;
							case 2:
								{
									chdirLocal(name);
									reloadLocalList();
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
											this.choosenLocalListViewIsFile = true;
										} else if (typeButton.equals("📁")) {
											this.choosenLocalListViewIsFile = false;
										} else {
											this.ftpMainView.showMessage("type file/folder Error");
										}
						        		this.choosenLocalListView = name;
						        		
						        		this.rightClickLocal.show(label[0], e.getScreenX(), e.getScreenY());
									} else {
										this.choosenLocalListViewIsFile = false;
										this.choosenLocalListView = "";
									}
								}
								break;
						}
					}
	        	});
				Tooltip tooltip;
				if (fileName.equals("..")) {
					tooltip = new Tooltip("Back to previous folder (LOCAL)");
				} else {
					tooltip = new Tooltip((isFile ? "File: " : "Folder: ") + fileName + " (LOCAL)");
				}
				tooltip.setShowDelay(new Duration(200));
				Tooltip.install(label[0], tooltip);
	        	fileLocalListView.getItems().add(label[0]);
			}
        	
		} catch (Exception e) {
			e.printStackTrace();
			this.ftpMainView.showMessage("Error", e.getMessage());
		}
	};
	
	private void chdirLocal(String dir) {
		try {
			this.ftpMainModel.chdirLocal(dir);
		} catch (Exception e) {
			e.printStackTrace();
			this.ftpMainView.showMessage("Error", e.getMessage());
			// TODO: handle exception
		}
	}
	
	private void chdirRemote(String dir) {
		try {
			this.ftpMainModel.chdirRemote(dir);
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
									chdirRemote(name);
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
				Tooltip tooltip;
				if (fileName.equals("..")) {
					tooltip = new Tooltip("Back to previous folder (REMOTE)");
				} else {
					tooltip = new Tooltip((isFile ? "File: " : "Folder: ") + fileName + " (REMOTE)");
				}
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
		
		Tooltip tooltipCopyFromLocal2Remote = new Tooltip("Copy file/folder from (LOCAL) to (REMOTE)");
		tooltipCopyFromLocal2Remote.setShowDelay(new Duration(200));
		Tooltip.install(this.copyFromLocal2RemoteImg, tooltipCopyFromLocal2Remote);
		
		Tooltip tooltipCopyFromRemote2Local = new Tooltip("Copy file/folder from (REMOTE) to (LOCAL)");
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
		
		Tooltip tooltipMoveFromLocal2Remote = new Tooltip("Move file/folder from (LOCAL) to (REMOTE)");
		tooltipMoveFromLocal2Remote.setShowDelay(new Duration(200));
		Tooltip.install(this.moveFromLocal2RemoteImg, tooltipMoveFromLocal2Remote);
		
		Tooltip tooltipMoveFromRemote2Local = new Tooltip("Move file/folder from (REMOTE) to (LOCAL)");
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
		
		Tooltip tooltipRenameLocal = new Tooltip("Rename file/folder (LOCAL)");
		tooltipRenameLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.renameLocalImg, tooltipRenameLocal);
		
		Tooltip tooltipRenameRemote = new Tooltip("Rename file/folder (REMOTE)");
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
		
		Tooltip tooltipReloadLocal = new Tooltip("Refresh (LOCAL)");
		tooltipReloadLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.reloadLocalImg, tooltipReloadLocal);
		
		Tooltip tooltipReloadRemote = new Tooltip("Refresh (REMOTE)");
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
		
		Tooltip tooltipMkdirLocal = new Tooltip("Create a folder (LOCAL)");
		tooltipMkdirLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.mkdirLocalImg, tooltipMkdirLocal);
		
		Tooltip tooltipMkdirRemote = new Tooltip("Create a folder (REMOTE)");
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
		
		Tooltip tooltipDeleteLocal = new Tooltip("Delete a file/folder (LOCAL)");
		tooltipDeleteLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.deleteLocalImg, tooltipDeleteLocal);
		
		Tooltip tooltipDeleteRemote = new Tooltip("Delete a file/folder (REMOTE)");
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
		
		Tooltip tooltipPropertiesLocal = new Tooltip("Properties file/folder (LOCAL)");
		tooltipPropertiesLocal.setShowDelay(new Duration(200));
		Tooltip.install(this.propertiesLocalImg, tooltipPropertiesLocal);
		
		Tooltip tooltipPropertiesRemote = new Tooltip("Properties file/folder (REMOTE)");
		tooltipPropertiesRemote.setShowDelay(new Duration(200));
		Tooltip.install(this.propertiesRemoteImg, tooltipPropertiesRemote);
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
		String newName = this.ftpMainView.showTextInput("Rename [" + oldName + "] (REMOTE)", "Input new name for [" + oldName + "]", "New name: ", oldName);
		
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
		String oldName = this.choosenLocalListView;
		if (oldName == null || oldName.isEmpty()) {
			return;
		}
		
		if (oldName.equals("..")) {
			return;
		}
		String newName = this.ftpMainView.showTextInput("Rename [" + oldName + "] (LOCAL)", "Input new name for [" + oldName + "]", "New name: ", oldName);
		
		if (newName == null || newName.isEmpty()) {
			return;
		}
		
		try {
			if (this.ftpMainModel.renameLocal(oldName, newName)) {
				reloadLocalList();
			} else {
				this.ftpMainView.showMessage("Error", "Cannot rename to [" + newName + "]");
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			this.ftpMainView.showMessage("Error", e1.getMessage());
		}
	}

	private void mkdirLocal() {
		String folderName = this.ftpMainView.showTextInput("Create folder (LOCAL)", "Input folder name", "Name: ");
		
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
		String folderName = this.ftpMainView.showTextInput("Create folder (REMOTE)", "Input folder name", "Name: ");
		
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
		
		if (this.ftpMainView.askYesOrNo("Delete " + name, "Delete " + name + "? [" + choosenLocalListView + "]", "LOCAL Path: \"" + this.localPath + "\"")) {
			if (this.choosenLocalListViewIsFile) {
				this.ftpMainModel.deleteFileLocal(choosenLocalListView);
			} else {
				this.ftpMainModel.deleteFolderLocal(choosenLocalListView);
			}
			
			if (this.choosenLocalListViewIsFile) {
				this.ftpMainModel.deleteFileLocal(choosenLocalListView);
			} else {
				this.ftpMainModel.deleteFolderLocal(choosenLocalListView);
			}
			reloadLocalList();
		}
	}
	
	private void deleteRemote() {
		String name = (this.choosenRemoteListViewIsFile) ? "file" : "folder";
		
		if (this.ftpMainView.askYesOrNo("Delete " + name, "Delete " + name + "? [" + choosenRemoteListView + "]", "REMOTE Path: \"" + this.remotePath + "\"")) {
			try {
				if (this.choosenRemoteListViewIsFile) {
					if (!this.ftpMainModel.deleteFileRemote(choosenRemoteListView)) {
						this.ftpMainView.showMessage("Error", "Cannot delete file [" + choosenRemoteListView + "]");
					}
				} else {
					if (!this.ftpMainModel.deleteFolderRemote(choosenRemoteListView)) {
						this.ftpMainView.showMessage("Error", "Cannot delete folder [" + choosenRemoteListView + "]");
					}
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
		this.ftpMainModel = new FTPMainModel("D:\\");
		
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
			String path = this.ftpMainView.showDialogChooseFolder(this.primaryStage);
			
			if (path == null || path.isEmpty()) {
				return;
			}
			
			this.ftpMainModel.setLocalPath(path);
			reloadLocalList();
		});
		
		this.menuFileExit.setOnAction(e -> {
			primaryStage.close();
		});
		
		// help
		this.menuHelpAbout.setOnAction(e -> {
			this.ftpMainView.showMessage("ABOUT");
		});
	}

	private void prepareContextMenu() {
		// local
		this.rightClickLocal = new ContextMenu();
        MenuItem renameItemLocal = new MenuItem("Rename");
        MenuItem deleteItemLocal = new MenuItem("Delete");
        MenuItem propertiesItemLocal = new MenuItem("Properties");

        // Add items to the context menu
        this.rightClickLocal.getItems().addAll(renameItemLocal, deleteItemLocal, propertiesItemLocal);

        // Set actions for menu items (optional)
        renameItemLocal.setOnAction(e -> {
        	renameLocal();
        });
        deleteItemLocal.setOnAction(e -> {
        	deleteLocal();
        });
        propertiesItemLocal.setOnAction(e -> {
        	propertiesLocal();
        });
		
		// remote
		// Create a context menu with some menu items
        this.rightClickRemote = new ContextMenu();
        MenuItem renameItemRemote = new MenuItem("Rename");
        MenuItem deleteItemRemote = new MenuItem("Delete");
        MenuItem propertiesItemRemote = new MenuItem("Properties");

        // Add items to the context menu
        this.rightClickRemote.getItems().addAll(renameItemRemote, deleteItemRemote, propertiesItemRemote);

        // Set actions for menu items (optional)
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
