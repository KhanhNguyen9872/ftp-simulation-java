package view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FTPLoginView {
 
	public FTPLoginView() {

	}
	
	public void showMessage(String title, String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.setContentText(msg);

        alert.showAndWait();
	}
	
	public void showMessage(String msg) {
		if (msg.contains("Cannot connect to ")) {
			msg = "Cannot connect to FTPServer";
		}
		this.showMessage("MESSAGE", msg);
	}
}
