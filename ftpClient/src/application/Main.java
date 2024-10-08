package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import run.FTPLoginRun;

public class Main extends Application {	
	@Override
	public void start(Stage primaryStage) throws Exception {
		new FTPLoginRun(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
