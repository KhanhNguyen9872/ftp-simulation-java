package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.FTPServerModel;

public class FTPServerController implements Initializable {
	@FXML
	private TextField textFieldHomePath;
	@FXML
	private TextField textFieldPort;
	
	@FXML
	private TextArea textFieldInformation;
	
	@FXML
	private Button buttonStart;
	@FXML
	private Button buttonStop;
	@FXML
	private Button buttonBrowse;
	
	@FXML
	private Label labelStatus;
	
	private FTPServerView view;
    private FTPServerModel model;
    private Stage primaryStage;

    public FTPServerController() {
        
    }
    
    

    public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	private void start() {
        try {
        	this.view.writeLog("Starting server...");
        	this.model.saveData();
        	
        	if (this.model.start()) {
        		this.textFieldPort.setEditable(false);
            	this.buttonBrowse.setDisable(true);
            	this.buttonStart.setDisable(true);
            	this.buttonStop.setDisable(false);
            	this.labelStatus.setText("RUNNING");
            	this.labelStatus.setStyle("-fx-text-fill: green;");
            	
            	this.view.writeLog("Server started!");
        	}
        	
			// this.model.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void stop() {
    	this.view.writeLog("Stopping server...");
    	this.model.stop();
    	
    	this.textFieldPort.setEditable(true);
    	this.buttonBrowse.setDisable(false);
    	this.buttonStart.setDisable(false);
    	this.buttonStop.setDisable(true);
    	this.labelStatus.setText("STOPPED");
    	this.labelStatus.setStyle("-fx-text-fill: red;");
    	this.view.writeLog("Server stopped!");
    }
    
    private void browse() {
    	String path = this.view.showDialogChooseFolder(this.primaryStage, this.textFieldHomePath.getText());
		
		if (path == null || path.isEmpty()) {
			return;
		}
		
		this.textFieldHomePath.setText(path);
		this.model.setPath(path);
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.view = new FTPServerView();
		this.model = new FTPServerModel();
		this.model.setView(view);
		
		this.view.setTextAreaLog(textFieldInformation);
		
		initializeButton();
		initializeTextField();
		readSettings();
	}

	private void initializeTextField() {
		// TODO Auto-generated method stub
		this.textFieldPort.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	if (newValue.isEmpty()) {
            		return;
            	}
            	
            	int value = -1;
            	try {
            		value = Integer.parseInt(newValue);
				} catch (Exception e) {
					// TODO: handle exception
					textFieldPort.setText(oldValue);
					return;
				}
            	
                if (value < 1 || value > 65535) {
                	textFieldPort.setText("21");
                	return;
                }
                
                model.setPort(value);
            }
        });
	}



	private void readSettings() {
		try {
			this.model.prepareSettings();
			this.view.writeLog("Read settings completed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.view.showMessageError("Cannot read settings from database");
		}
		
		this.textFieldHomePath.setText(model.getPath());
		this.textFieldPort.setText(String.valueOf(model.getPort()));
	}

	private void initializeButton() {
		this.buttonStop.setDisable(true);
		// TODO Auto-generated method stub
		this.buttonStart.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				start();
			}
		});
		this.buttonStop.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				stop();
			}
		});
		this.buttonBrowse.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				browse();
			}
		});
	}
}
