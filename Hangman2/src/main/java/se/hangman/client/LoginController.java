package se.hangman.client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController {

	public ClientLogin login = new ClientLogin();
	public Stage loginStage = null;
	public ClientSocket clientSocket = null;

	@FXML
	private TextField textUser;

	@FXML
	private TextField textPass;

	@FXML
	private Button btnLogin;

	@FXML
	void EventOnPassField(KeyEvent event) {

	}

	@FXML
	void validateLogin(ActionEvent event) {
		btnLogin.setDisable(true);
		String username = textUser.getText();
		String pswd = textPass.getText();
		if (username.isEmpty() || username == null || pswd.isEmpty() || pswd == null) {
			Alert alert = new Alert(AlertType.INFORMATION, "Username and Password Fields are mandatory to login");
			alert.showAndWait();
			btnLogin.setDisable(false);
		} else {
			 String loginString = login.validateLogin(username, pswd);
			 try {
				clientSocket.writeDataToServerFromLoginController(loginString,"login", this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void response(String recvdString) {
		System.out.println(recvdString);
		
		if (recvdString.equalsIgnoreCase("201")) {
			Alert alert = new Alert(AlertType.INFORMATION,
					"Invalid Username or Password...");
			alert.showAndWait();
			textUser.setText("");
			textPass.setText("");
			btnLogin.setDisable(false);
		} else {
			login.jwtKey = recvdString;
			//loginStage.close();
			Platform.runLater(() -> {
				Main.startGameUI(login.PlayerName, this.clientSocket, recvdString);
			});
			
			
		}
	}

}
