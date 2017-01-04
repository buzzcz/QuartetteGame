package cz.zcu.kiv.ups.utils;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

/**
 * Utility to create and show alerts and dialogs.
 *
 * @author Jaroslav Klaus
 */
public class AlertsAndDialogs {

	/**
	 * Private constructor to disable instantiation.
	 */
	private AlertsAndDialogs() {
	}

	/**
	 * Creates alert with specified attributes.
	 *
	 * @param type    type of alert.
	 * @param title   title of alert.
	 * @param header  header of alert.
	 * @param content content of alert.
	 * @return created alert.
	 */
	private static Alert createAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}

	/**
	 * Creates, shows alert and waits for alert to be dismissed.
	 *
	 * @param type    type of alert.
	 * @param title   title of alert.
	 * @param header  header of alert.
	 * @param content content of alert.
	 * @return result of showAndWait.
	 */
	public static Optional<ButtonType> showAndWaitAlert(Alert.AlertType type, String title, String header, String
			content) {
		Alert alert = createAlert(type, title, header, content);
		return alert.showAndWait();
	}

	/**
	 * Creates and shows alert.
	 *
	 * @param type    type of alert.
	 * @param title   title of alert.
	 * @param header  header of alert.
	 * @param content content of alert.
	 */
	public static void showAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = createAlert(type, title, header, content);
		alert.show();
	}

	/**
	 * Creates, shows Choice Dialog and waits for dialog to be dismissed.
	 *
	 * @param defaultValue default value to select.
	 * @param choices      choices to select from.
	 * @param title        title of dialog.
	 * @param header       header of dialog.
	 * @param content      content of dialog.
	 * @return result of showAndWait.
	 */
	public static <T> Optional<T> showAndWaitChoiceDialog(T defaultValue, List<T> choices, String title, String
			header, String content) {
		ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultValue, choices);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		return dialog.showAndWait();
	}

	/**
	 * Creates, shows custom dialog for creating game and waits for dialog to be dismissed.
	 *
	 * @return result of showAndWait.
	 */
	public static Optional<Pair<Integer, String>> showAndWaitChoiceTextDialog(String title, String header, String
			content1, List<Integer> choices, Integer defaultValue, String content2, String prompt, String value) {
		Dialog<Pair<Integer, String>> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		GridPane gridPane = new GridPane();

		Label label1 = new Label(content1);
		gridPane.add(label1, 0, 0);
		ChoiceBox<Integer> choiceBox = new ChoiceBox<>(FXCollections.observableList(choices));
		if (defaultValue != null) {
			choiceBox.getSelectionModel().select(defaultValue);
		} else {
			choiceBox.getSelectionModel().selectFirst();
		}
		GridPane.setHgrow(choiceBox, Priority.SOMETIMES);
		gridPane.add(choiceBox, 1, 0);

		Label label2 = new Label(content2);
		gridPane.add(label2, 0, 1);
		TextField textField = new TextField();
		textField.setPromptText(prompt);
		textField.setText(value);
		GridPane.setHgrow(textField, Priority.SOMETIMES);
		gridPane.add(textField, 1, 1);

		dialog.getDialogPane().setContent(gridPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				return new Pair<>(choiceBox.getSelectionModel().getSelectedItem(), textField.getText());
			}
			return null;
		});
		return dialog.showAndWait();
	}

	/**
	 * Creates, shows Text Inut Dialog and waits for dialog to be dismissed.
	 *
	 * @param title  title to show.
	 * @param header header to show.
	 * @param prompt prompt to show.
	 * @return result of showAndWait.
	 */
	public static Optional<String> showAndWaitTextInputDialog(String title, String header, String content, String
			prompt, String value) {
		TextInputDialog dialog = new TextInputDialog(value);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		dialog.getEditor().setPromptText(prompt);
		return dialog.showAndWait();
	}

}
