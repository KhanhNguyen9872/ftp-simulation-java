package run;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.FTPLoginController;

public class FTPLoginRun {
	public FTPLoginRun(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		primaryStage.setTitle("Login | FTP Simulation");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		
		FTPLoginController controller = loader.getController();
		controller.setStage(primaryStage);
		
		primaryStage.show();
	}
}
