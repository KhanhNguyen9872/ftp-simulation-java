package view;

import java.util.Optional;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;


public class FTPMainView {
	
	public FTPMainView() {
		
	}
	
	public void showMessage(String title, String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.setContentText(msg);

        alert.showAndWait();
	}
	
	public void showMessage(String msg) {
		this.showMessage("MESSAGE", msg);
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

}
