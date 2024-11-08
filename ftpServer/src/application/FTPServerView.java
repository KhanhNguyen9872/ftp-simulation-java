package application;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FTPServerView {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private TextArea textAreaLog;
	
	public FTPServerView() {
		
	}
	
	public void setTextAreaLog(TextArea textAreaLog) {
		this.textAreaLog = textAreaLog;
		this.textAreaLog.setText("");
	}
	
	public void writeLog(String text) {
		this.textAreaLog.setText(this.textAreaLog.getText() + "[" + getCurrentDateTime() + "] " + text + "\n");
		this.textAreaLog.setScrollTop(Double.MAX_VALUE);
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
	
	public String showDialogChooseFolder(Stage primaryStage, String path) {		
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        
        if (!path.isEmpty()) {
        	File defaultDirectory = new File(path);
        	directoryChooser.setInitialDirectory(defaultDirectory);
        }

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        }
        
        return null;
	}
	
	
	public String showDialogChooseFolder(Stage primaryStage) {		
        return this.showDialogChooseFolder(primaryStage, "");
	}
}
