module ftpClient {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires java.base;
	
	opens run to javafx.graphics, javafx.fxml;
	opens controller to javafx.graphics, javafx.fxml;
	opens application to javafx.graphics, javafx.fxml;
}
