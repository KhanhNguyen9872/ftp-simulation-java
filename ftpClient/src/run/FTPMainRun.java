package run;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import controller.FTPMainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class FTPMainRun {
	public FTPMainRun(Socket sock) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("File Explorer | FTP Simulation");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		
		FTPMainController controller = loader.getController();
		controller.setSocket(sock);
		
		primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
            alert.setTitle("Confirm Exit");
            alert.setHeaderText(null);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
					sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } else {
                event.consume();
            };
        });
		
		primaryStage.show();
		controller.run();
	};
};
