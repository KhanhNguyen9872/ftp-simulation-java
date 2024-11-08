package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.FTPServerModel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class FTPServerApp extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
			Parent parent = loader.load();
			Scene scene = new Scene(parent);
			primaryStage.setTitle("Server | FTP Simulation");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			
			FTPServerController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
