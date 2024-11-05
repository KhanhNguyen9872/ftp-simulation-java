package view;

import java.util.Optional;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class FTPMainView {
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private TextArea textAreaLog;
	
	public FTPMainView() {
		
	}
	
	public void setTextAreaLog(TextArea textAreaLog) {
		this.textAreaLog = textAreaLog;
		this.textAreaLog.setText("");
	}
	
	private void writeLog(String text) {
		new Thread(() -> {
			this.textAreaLog.setText(this.textAreaLog.getText() + text + "\n");
			this.textAreaLog.setScrollTop(Double.MAX_VALUE);
		}).start();
	}
	
	public void writeLog(String head, String text) {
		writeLog("[" + getCurrentDateTime() + "]" + (!head.isEmpty() ? " " + head + ": " : " ") + text);
	}
	
	private String getCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();

        return currentDateTime.format(formatter);
	}
	
	public void showMessageError(String msg) {
		this.showMessageError("", msg);
	}
	
	public void showMessageError(String head, String msg) {
		writeLog("[" + getCurrentDateTime() + "]" + (!head.isEmpty() ? " " + head + ": " : " ") + "ERROR: " + msg);
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERROR" + (!head.isEmpty() ? " (" + head + ")" : ""));
        alert.setHeaderText(null); 
        alert.setContentText(msg);
        alert.showAndWait();
		
	}
	
	public void showMessage(String msg) {
		this.showMessage("", msg);
	}
	
	public void showMessage(String head, String msg) {
		writeLog("[" + getCurrentDateTime() + "]" + (!head.isEmpty() ? " " + head + ": " : " ") + "MESSAGE: " + msg);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("MESSAGE" + (!head.isEmpty() ? " (" + head + ")" : ""));
        alert.setHeaderText(null); 
        alert.setContentText(msg);
        alert.showAndWait();
	}
	
	public boolean askYesOrNo(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == yesButton) {
            return true;
        }
        return false;
	}
	
	public String showTextInput(String title, String header, String content) {
		return this.showTextInput(title, header, content, "");
	}
	
	public String showTextInput(String title, String header, String content, String defaultInput) {
		String s = null;
		TextInputDialog dialog = new TextInputDialog(defaultInput);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        // Show the dialog and capture the result
        Optional<String> result = dialog.showAndWait();
        
        try {
        	s = result.get();
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        return s;
	}
	
	public String showDialogChooseFolder(Stage primaryStage) {		
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        }
        
        return null;
	}
	
	public void showProperties(String host, String path, String name, String size, String lastModifiedTime) {
		Label hostLabel = new Label("Host: " + host);
		Label pathLabel = new Label("Path: " + path);
        Label nameLabel = new Label("Name: " + name);
        Label sizeLabel = null;
        if (size != null) {
        	sizeLabel = new Label("Size: " + size);
        }
        Label lastModifiedLabel = new Label("Last Modified Time: " + lastModifiedTime);

        // Layout for the labels
        VBox vbox = new VBox(10);
        vbox.getChildren().add(hostLabel);
        vbox.getChildren().add(pathLabel);
        vbox.getChildren().add(nameLabel);
        if (sizeLabel != null) {
        	vbox.getChildren().add(sizeLabel);
        }
        vbox.getChildren().add(lastModifiedLabel);
        vbox.setStyle("-fx-padding: 10;");
        
		Stage detailsStage = new Stage();
        detailsStage.setTitle("Properties");
        detailsStage.setScene(new Scene(vbox));
        detailsStage.setResizable(false);
        detailsStage.show();
	}

}
