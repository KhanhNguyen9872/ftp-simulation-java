package controller;

import javafx.concurrent.Task;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	
	@FXML
	private TextArea textAreaLog;
	
	@FXML
	private TextField textFieldLocalPath;
	@FXML
	private TextField textFieldRemotePath;
	
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
		this.textFieldLocalPath.setText(this.localPath);
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
					Label lb = (Label)e.getSource();
	        		String buttonName = lb.getText();
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
											this.ftpMainView.showMessage("LOCAL", "type file/folder Error");
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
									if (!buttonName.equals("..")) {
										String typeButton = buttonName.substring(0, 2);
										if (typeButton.equals("📄")) {
											ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", "", this.localPath + "/" + name);
											try {
												Process process = processBuilder.start();
											} catch (Exception e2) {
												this.ftpMainView.showMessageError("LOCAL", e2.getMessage());
											}
											
											break;
										}
									}
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
											this.ftpMainView.showMessage("LOCAL", "type file/folder Error");
										}
						        		this.choosenLocalListView = name;
						        		
						                this.rightClickLocal.show(lb, e.getScreenX(), e.getScreenY());
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
			this.ftpMainView.showMessageError("LOCAL", e.getMessage());
		}
	};
	
	private void chdirLocal(String dir) {
		try {
			this.ftpMainModel.chdirLocal(dir);
		} catch (Exception e) {
			e.printStackTrace();
			this.ftpMainView.showMessageError(e.getMessage());
			// TODO: handle exception
		}
	}
	
	private void chdirRemote(String dir) {
		try {
			this.ftpMainModel.chdirRemote(dir);
		} catch (Exception e) {
			e.printStackTrace();
			this.ftpMainView.showMessageError(e.getMessage());
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
					Label lb = (Label)e.getSource();
					String buttonName = lb.getText();
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
											this.ftpMainView.showMessage("REMOTE", "type file/folder Error");
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
									if (!buttonName.equals("..")) {
										String typeButton = buttonName.substring(0, 2);
										if (typeButton.equals("📄")) {
											this.ftpMainView.showMessageError("REMOTE", "Open file in REMOTE isn't supported at now!");
											break;
										}
									}
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
											this.ftpMainView.showMessage("REMOTE", "type file/folder Error");
										}
						        		this.choosenRemoteListView = name;
						        		
						        		this.rightClickRemote.show(lb, e.getScreenX(), e.getScreenY());
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
			this.ftpMainView.showMessageError("REMOTE", e.getMessage());
			e.printStackTrace();
		};
	};
	
	private void getCurrentRemotePath() {
		try {
			this.remotePath = this.ftpMainModel.getCurrentRemotePath();
			this.textFieldRemotePath.setText(this.remotePath);
		} catch (Exception e) {
			this.ftpMainView.showMessage("REMOTE", e.getMessage());
		};
	};
	
	private void prepareButton() {
		// copy
		this.copyFromLocal2RemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenLocalListView == null || choosenLocalListView.isEmpty()) {
					this.ftpMainView.showMessageError("LOCAL", "You must select one file/folder!");
					return;
				}
				
				this.copyLocalToRemote();
			}
		});
		
		this.copyFromRemote2LocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessageError("REMOTE", "You must select one file/folder!");
					return;
				}
				
				this.copyRemoteToLocal();
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
				if (choosenLocalListView == null || choosenLocalListView.isEmpty()) {
					this.ftpMainView.showMessageError("LOCAL", "You must select one file/folder!");
					return;
				}
				
				this.moveLocalToRemote();
			}
		});
		
		this.moveFromRemote2LocalImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessageError("REMOTE", "You must select one file/folder!");
					return;
				}
				
				this.moveRemoteToLocal();
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
					this.ftpMainView.showMessageError("LOCAL", "You must select one file/folder!");
					return;
				}
				
				this.renameLocal();
			}
		});
		this.renameRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessageError("REMOTE", "You must select one file/folder!");
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
					this.ftpMainView.showMessageError("LOCAL", "You must select one file/folder!");
					return;
				}
				
				this.deleteLocal();
			}
		});
		
		this.deleteRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessageError("REMOTE", "You must select one file/folder!");
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
					this.ftpMainView.showMessageError("LOCAL", "You must select one file/folder!");
					return;
				}
				
				this.propertiesLocal();
			}
		});
		this.propertiesRemoteImg.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (choosenRemoteListView == null || choosenRemoteListView.isEmpty()) {
					this.ftpMainView.showMessageError("REMOTE", "You must select one file/folder!");
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
	
	private void propertiesRemote() {
		String size = null;
		String time;
		
		try {
			if (this.choosenRemoteListViewIsFile) {
				size = this.ftpMainModel.convertBytes(Long.parseLong(this.ftpMainModel.getSizeFileRemote(choosenRemoteListView)));
			}
			
			time = this.ftpMainModel.timestampToDateTime(Long.parseLong(this.ftpMainModel.getLastModifiedTimeFileRemote(choosenRemoteListView)));
			
			this.ftpMainView.writeLog("REMOTE", "PROPERTIES [" + choosenRemoteListView + "]");
			this.ftpMainView.showProperties("REMOTE", this.remotePath, choosenRemoteListView, size, time);
		} catch (Exception e) {
			e.printStackTrace();
			this.ftpMainView.showMessageError("REMOTE", e.getMessage());
		}
	}

	private void propertiesLocal() {
		String size = null;
		String time;
		
		try {
			if (this.choosenLocalListViewIsFile) {
				size = this.ftpMainModel.convertBytes(this.ftpMainModel.getSizeFileLocal(choosenLocalListView));
			}
			
			time = this.ftpMainModel.timestampToDateTime(this.ftpMainModel.getLastModifiedTimeFileLocal(choosenLocalListView));
			
			this.ftpMainView.writeLog("LOCAL", "PROPERTIES [" + choosenLocalListView + "]");
			this.ftpMainView.showProperties("LOCAL", this.localPath, choosenLocalListView, size, time);
		} catch (Exception e) {
			e.printStackTrace();
			this.ftpMainView.showMessageError("LOCAL", e.getMessage());
		}
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
			this.ftpMainView.showMessageError("REMOTE", "New name must not empty");
			return;
		}
		
		if (this.ftpMainView.askYesOrNo("Rename [" + oldName + "] (REMOTE)", "Do you want to rename?", "Remote path: \"" + this.remotePath + "\"\nOld name: " + oldName + "\nNew name: " + newName)) {
			try {
				if (this.ftpMainModel.renameRemote(oldName, newName)) {
					reloadRemoteList();
					this.ftpMainView.showMessage("REMOTE", "Renamed successfully from [" + oldName + "] to [" + newName + "]");
				} else {
					this.ftpMainView.showMessageError("REMOTE", "Cannot rename to [" + newName + "]");
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				this.ftpMainView.showMessageError(e1.getMessage());
			}
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
			this.ftpMainView.showMessageError("LOCAL", "New name must not empty");
			return;
		}
		
		if (this.ftpMainView.askYesOrNo("Rename [" + oldName + "] (LOCAL)", "Do you want to rename?", "Local path: \"" + this.localPath + "\"\nOld name: " + oldName + "\nNew name: " + newName)) {
			try {
				if (this.ftpMainModel.renameLocal(oldName, newName)) {
					reloadLocalList();
					this.ftpMainView.showMessage("LOCAL", "Renamed successfully from [" + oldName + "] to [" + newName + "]");
				} else {
					this.ftpMainView.showMessageError("LOCAL", "Cannot rename to [" + newName + "]");
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				this.ftpMainView.showMessageError("LOCAL", e1.getMessage());
			}
		}
	}

	private void mkdirLocal() {
		String folderName = this.ftpMainView.showTextInput("Create folder (LOCAL)", "Input folder name", "Name: ");
		
		if (folderName == null || folderName.isEmpty()) {
			this.ftpMainView.showMessageError("LOCAL", "Folder name must not empty");
			return;
		}
		
		if (this.ftpMainModel.mkdirLocal(folderName)) {
			reloadLocalList();
			this.ftpMainView.showMessage("LOCAL", "Created folder [" + folderName + "]");
		} else {
			this.ftpMainView.showMessageError("LOCAL", "Cannot create folder [" + folderName + "]");
		}
	}
	
	private void mkdirRemote() {
		String folderName = this.ftpMainView.showTextInput("Create folder (REMOTE)", "Input folder name", "Name: ");
		
		if (folderName == null || folderName.isEmpty()) {
			this.ftpMainView.showMessageError("REMOTE", "Folder name must not empty");
			return;
		}
		
		try {
			if (this.ftpMainModel.mkdirRemote(folderName)) {
				reloadRemoteList();
				this.ftpMainView.showMessage("REMOTE", "Created folder [" + folderName + "]");
			} else {
				this.ftpMainView.showMessageError("REMOTE", "Cannot create folder [" + folderName + "]");
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			this.ftpMainView.showMessageError("REMOTE", e1.getMessage());
		}
	}
	
	private void deleteLocal() {
		String name = (this.choosenLocalListViewIsFile) ? "file" : "folder";
		
		if (this.ftpMainView.askYesOrNo("Delete " + name, "Delete " + name + "? [" + choosenLocalListView + "]", "LOCAL Path: \"" + this.localPath + "\"")) {
			if (this.choosenLocalListViewIsFile) {
				if (this.ftpMainModel.deleteFileLocal(choosenLocalListView)) {
					this.ftpMainView.showMessage("LOCAL", "Deleted file [" + choosenLocalListView + "]");
				} else {
					this.ftpMainView.showMessageError("LOCAL", "Cannot delete file [" + choosenLocalListView + "]");
				}
			} else {
				if (this.ftpMainModel.deleteFolderLocal(choosenLocalListView)) {
					this.ftpMainView.showMessage("LOCAL", "Deleted folder [" + choosenLocalListView + "]");
				} else {
					this.ftpMainView.showMessageError("LOCAL", "Cannot delete folder [" + choosenLocalListView + "]");
				}
			}
			
			reloadLocalList();
		}
	}
	
	private void deleteRemote() {
		String name = (this.choosenRemoteListViewIsFile) ? "file" : "folder";
		
		if (this.ftpMainView.askYesOrNo("Delete " + name, "Delete " + name + "? [" + choosenRemoteListView + "]", "REMOTE Path: \"" + this.remotePath + "\"")) {
			try {
				if (this.choosenRemoteListViewIsFile) {
					if (this.ftpMainModel.deleteFileRemote(choosenRemoteListView)) {
						this.ftpMainView.showMessage("REMOTE", "Deleted file [" + choosenRemoteListView + "]");
					} else {
						this.ftpMainView.showMessageError("REMOTE", "Cannot delete file [" + choosenRemoteListView + "]");
					}
				} else {
					if (this.ftpMainModel.deleteFolderRemote(choosenRemoteListView)) {
						this.ftpMainView.showMessage("REMOTE", "Deleted folder [" + choosenRemoteListView + "]");
					} else {
						this.ftpMainView.showMessageError("REMOTE", "Cannot delete folder [" + choosenRemoteListView + "]");
					}
				}
				
				reloadRemoteList();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				this.ftpMainView.showMessageError("REMOTE", e1.getMessage());
			}
		}
	}
	
	private void copyRemoteToLocal() {
		String fileName = choosenRemoteListView;
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		
		String type = (this.choosenRemoteListViewIsFile ? "file" : "folder");
		
		if (this.ftpMainView.askYesOrNo("Copy " + type, "Copy " + type + "[" + fileName + "] to LOCAL?", "Remote path: \"" + this.remotePath + "\"")) {
			try {
				this.ftpMainView.writeLog("REMOTE", "COPYING... [" + fileName + "]");
				Task<Boolean> copyTask = new Task<>() {
				    @Override
				    protected Boolean call() {
				        try {
				            if (choosenRemoteListViewIsFile) {
				                return ftpMainModel.receiveFile(fileName);
				            } else {
				                return ftpMainModel.receiveFolder(fileName);
				            }
				        } catch (Exception e) {
				            updateMessage("Error: " + e.getMessage());
				            return false;
				        }
				    }
				};
				
				copyTask.setOnSucceeded(event -> {
				    boolean done = copyTask.getValue();
				    if (done) {
				        ftpMainView.showMessage("REMOTE", "Completed copy " + type + " [" + fileName + "] to LOCAL");
				        reloadLocalList();
				    } else {
				        ftpMainView.showMessageError("REMOTE", "Cannot copy " + type + " [" + fileName + "] to LOCAL");
				    }
				});

				// Run the task in a background thread
				new Thread(copyTask).start();
			} catch (Exception e) {
				this.ftpMainView.showMessageError("REMOTE", e.getMessage());
			}
		}
	}
	
	private void copyLocalToRemote() {
		String fileName = choosenLocalListView;
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		
		String type = (this.choosenLocalListViewIsFile ? "file" : "folder");
		
		if (this.ftpMainView.askYesOrNo("Copy " + type, "Copy " + type + " [" + fileName + "] to REMOTE?", "Local path: \"" + this.localPath + "\"")) {
			try {
				this.ftpMainView.writeLog("LOCAL", "COPYING... [" + fileName + "]");
				Task<Boolean> copyTask = new Task<>() {
				    @Override
				    protected Boolean call() {
				        try {
				        	if (choosenLocalListViewIsFile) {
								return ftpMainModel.sendFile(fileName);
							} else {
								return ftpMainModel.sendFolder(fileName);
							}
				        } catch (Exception e) {
				            updateMessage("Error: " + e.getMessage());
				            return false;
				        }
				    }
				};
				
				copyTask.setOnSucceeded(event -> {
				    boolean done = copyTask.getValue();
				    if (done) {
				    	ftpMainView.showMessage("LOCAL", "Completed copy " + type + " [" + fileName + "] to REMOTE");
						reloadRemoteList();
				    } else {
						ftpMainView.showMessageError("LOCAL", "Cannot copy " + type + " [" + fileName + "] to REMOTE");
				    }
				});

				// Run the task in a background thread
				new Thread(copyTask).start();
			} catch (Exception e) {
				this.ftpMainView.showMessageError("LOCAL", e.getMessage());
			}
		}
	}
	
	private void moveLocalToRemote() {
		String fileName = choosenLocalListView;
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		
		String type = (this.choosenLocalListViewIsFile ? "file" : "folder");
		
		if (this.ftpMainView.askYesOrNo("Move " + type, "Move " + type + " [" + fileName + "] to REMOTE?", "Local path: \"" + this.localPath + "\"")) {
			try {
				this.ftpMainView.writeLog("LOCAL", "MOVING... [" + fileName + "]");
				Task<Boolean> copyTask = new Task<>() {
				    @Override
				    protected Boolean call() {
				        try {
				        	if (choosenLocalListViewIsFile) {
								return ftpMainModel.sendFile(fileName);
							} else {
								return ftpMainModel.sendFolder(fileName);
							}
				        } catch (Exception e) {
				            updateMessage("Error: " + e.getMessage());
				            return false;
				        }
				    }
				};
				
				copyTask.setOnSucceeded(event -> {
				    boolean done = copyTask.getValue();
				    if (done) {
				    	ftpMainModel.deleteFileLocal(fileName);
						ftpMainView.showMessage("LOCAL", "Completed move " + type + " [" + fileName + "] to REMOTE");
						reloadLocalList();
						reloadRemoteList();
				    } else {
						ftpMainView.showMessageError("LOCAL", "Cannot move " + type + " [" + fileName + "] to REMOTE");
				    }
				});

				// Run the task in a background thread
				new Thread(copyTask).start();
			} catch (Exception e) {
				this.ftpMainView.showMessageError("LOCAL", e.getMessage());
			}
		}
	}
	
	private void moveRemoteToLocal() {
		String fileName = choosenRemoteListView;
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		
		String type = (this.choosenRemoteListViewIsFile ? "file" : "folder");
		
		if (this.ftpMainView.askYesOrNo("Move file", "Move " + type + " [" + fileName + "] to LOCAL?", "Remote path: \"" + this.remotePath + "\"")) {
			try {
				this.ftpMainView.writeLog("REMOTE", "MOVING... [" + fileName + "]");
				Task<Boolean> copyTask = new Task<>() {
				    @Override
				    protected Boolean call() {
				        try {
				            if (choosenRemoteListViewIsFile) {
				                return ftpMainModel.receiveFile(fileName);
				            } else {
				                return ftpMainModel.receiveFolder(fileName);
				            }
				        } catch (Exception e) {
				            updateMessage("Error: " + e.getMessage());
				            return false;
				        }
				    }
				};
				
				copyTask.setOnSucceeded(event -> {
				    boolean done = copyTask.getValue();
				    if (done) {
				    	try {
				    		ftpMainModel.deleteFileRemote(fileName);
						} catch (Exception e) {
							// TODO: handle exception
						}
				    	
				    	ftpMainView.showMessage("REMOTE", "Completed move " + type + " [" + fileName + "] to LOCAL");
						reloadLocalList();
						reloadRemoteList();
				    } else {
						ftpMainView.showMessageError("REMOTE", "Cannot move " + type + " [" + fileName + "] to LOCAL");
				    }
				});

				// Run the task in a background thread
				new Thread(copyTask).start();
			} catch (Exception e) {
				this.ftpMainView.showMessageError("REMOTE", e.getMessage());
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
		
		this.ftpMainView.setTextAreaLog(textAreaLog);
	}
	
	private void prepareMenuItem() {
		// file
		this.menuFileOpenLocal.setOnAction(e -> {
			String path = this.ftpMainView.showDialogChooseFolder(this.primaryStage);
			
			if (path == null || path.isEmpty()) {
				return;
			}
			
			this.ftpMainModel.setLocalPath(path);
			this.ftpMainView.writeLog("LOCAL", "OPEN: \"" + path + "\"");
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
		MenuItem copyItemToRemote = new MenuItem("Copy to REMOTE");
		MenuItem moveItemToRemote = new MenuItem("Move to REMOTE");
        MenuItem renameItemLocal = new MenuItem("Rename");
        MenuItem deleteItemLocal = new MenuItem("Delete");
        MenuItem propertiesItemLocal = new MenuItem("Properties");

        // Add items to the context menu
        this.rightClickLocal.getItems().addAll(copyItemToRemote, moveItemToRemote, renameItemLocal, deleteItemLocal, propertiesItemLocal);

        // Set actions for menu items (optional)
        copyItemToRemote.setOnAction(e -> {
        	copyLocalToRemote();
        });
        moveItemToRemote.setOnAction(e -> {
        	moveLocalToRemote();
        });
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
        MenuItem copyItemToLocal = new MenuItem("Copy to LOCAL");
		MenuItem moveItemToLocal = new MenuItem("Move to LOCAL");
        MenuItem renameItemRemote = new MenuItem("Rename");
        MenuItem deleteItemRemote = new MenuItem("Delete");
        MenuItem propertiesItemRemote = new MenuItem("Properties");

        // Add items to the context menu
        this.rightClickRemote.getItems().addAll(copyItemToLocal, moveItemToLocal, renameItemRemote, deleteItemRemote, propertiesItemRemote);

        // Set actions for menu items (optional)
        copyItemToLocal.setOnAction(e -> {
        	copyRemoteToLocal();
        });
        moveItemToLocal.setOnAction(e -> {
        	moveRemoteToLocal();
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
