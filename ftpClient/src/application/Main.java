package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import login.*;

public class Main extends Application {	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FTPLoginView loginView = new FTPLoginView(primaryStage);
		loginView.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
