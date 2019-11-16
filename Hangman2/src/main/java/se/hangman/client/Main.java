package se.hangman.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/fxml/ClientUI.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Client");
			primaryStage.resizableProperty().setValue(false);
			primaryStage.setOnCloseRequest(event -> {
				Alert alert = new Alert(AlertType.CONFIRMATION,
						"Are you sure you want to stop Playing.. All players will lose their score", ButtonType.YES,
						ButtonType.NO);
				alert.showAndWait();
				if (alert.getResult() == ButtonType.NO) {
					event.consume();
				} else {
					Platform.exit();
				}
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void startGameUI(String player, ClientSocket cs, String Key) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/GameUI.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setTitle("Haugman Game : " + player);
			stage.setScene(new Scene(root));
			stage.resizableProperty().setValue(false);
			stage.setOnCloseRequest(event -> {
				event.consume();
			});
			stage.show();
			GameController gc = (GameController) fxmlLoader.getController();
			gc.gm.PlayerName = player;
			gc.clientSocket = cs;
			gc.gm.jwtKey = Key;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startLoginUI(String player, ClientSocket cs) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/LoginUI.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage Loginstage = new Stage();
			Loginstage.setTitle("Login Page : " + player);
			Loginstage.setScene(new Scene(root));
			Loginstage.resizableProperty().setValue(false);
			Loginstage.setOnCloseRequest(event -> {
				cs.close();
			});
			Loginstage.show();
			LoginController lc = (LoginController) fxmlLoader.getController();
			lc.loginStage = Loginstage;
			lc.clientSocket = cs;
			lc.login.PlayerName = player;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Welcome to Hangman Game");
		launch(args);
	}
}
