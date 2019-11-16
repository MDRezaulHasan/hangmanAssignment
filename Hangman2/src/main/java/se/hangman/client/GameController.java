package se.hangman.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import se.hangman.client.GameModel.Message;
import javafx.scene.control.Alert.AlertType;

public class GameController {

	public GameModel gm = new GameModel();
	public ClientSocket clientSocket = null;
	private int NoOfLetters = 0;

	@FXML
	private Button btnStart;

	@FXML
	private Button btnStop;

	@FXML
	private Button btnGuess;

	@FXML
	private TextField textWord;

	@FXML
	private TextField textRem;

	@FXML
	private TextField textScore;

	@FXML
	private TextField textGuess;

	@FXML
	void startGame(ActionEvent event) throws IOException {
		btnStart.setDisable(true);
		String msg = gm.sendStartMessage();
		clientSocket.writeDataToServerFromGameController(msg, "start",this);
	}

	void responseOfStart(String response) throws IOException {
		Message msg = gm.JSONStringToObject(response);
		if (msg == null) {
			Alert alert = new Alert(AlertType.INFORMATION, "Message Vallidation Failed.. so closing the Window");
			alert.showAndWait();
			stopGame();
		} else {
			textWord.setText(msg.correctGuessedLetters);
			textRem.setText(Integer.toString(msg.NoOfAttemptsLeft));
			textScore.setText(Integer.toString(msg.Score));
			NoOfLetters = msg.NoOfLetters;
			btnGuess.setDisable(false);
		}
	}
	void stopGame() throws IOException {
		String stop= gm.sendStopMessage();
		clientSocket.writeDataToServerFromGameController(stop, "stop",this);
		clientSocket.close();
		ClientModel.removeFromPlayersList(gm.PlayerName);
		Stage stage = (Stage) btnStart.getScene().getWindow();
		stage.close();
	}
	@FXML
	void stopGame(ActionEvent event) throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION,
				"Are you sure you want to stop Playing.. You will lose the Score", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			gm.sendStopMessage();
			Stage stage = (Stage) btnStop.getScene().getWindow();
			stage.close();
		}
	}

	@FXML
	void guessWord(ActionEvent event) throws IOException {
		String guessStr = textGuess.getText();
		if (guessStr.length() == 1 || guessStr.length() == NoOfLetters) {
			String msg = gm.sendGuessMessage(guessStr);
			clientSocket.writeDataToServerFromGameController(msg, "guess",this);
		} else {
			Alert alert = new Alert(AlertType.INFORMATION,
					"Guess can be letter or a word same size as the word to be guessed");
			alert.showAndWait();
		}
		textGuess.setText("");
	}

	void responseForGuess(String resonse) throws IOException {
		Message msg = gm.JSONStringToObject(resonse);
		if (msg == null) {
			Alert alert = new Alert(AlertType.INFORMATION, "Message Vallidation Failed.. so closing the Window");
			alert.showAndWait();
			stopGame();
		} else {
			textWord.setText(msg.correctGuessedLetters);
			textScore.setText(Integer.toString(msg.Score));
			if (msg.NoOfAttemptsLeft == -1) {
				textRem.setText("NA");
				btnStart.setDisable(false);
				btnGuess.setDisable(true);
			} else if (msg.NoOfAttemptsLeft == 0) {
				textRem.setText("0");
				btnStart.setDisable(false);
				btnGuess.setDisable(true);
			} else {
				textRem.setText(Integer.toString(msg.NoOfAttemptsLeft));
			}
		}
	}
	public GameModel getGameModelObject() {
		return gm;
	}

}
