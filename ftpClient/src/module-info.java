module ftpClient {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	
	opens login to javafx.graphics, javafx.fxml;
	opens lib to javafx.graphics, javafx.fxml;
	opens main to javafx.graphics, javafx.fxml;
	opens application to javafx.graphics, javafx.fxml;
}
